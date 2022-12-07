/*
 * Copyright 2019 - 2025 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.openknowledge.sample.address.infrastructure;

import static java.lang.Math.pow;
import static java.util.Arrays.stream;
import static java.util.Optional.of;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.geronimo.microprofile.metrics.common.prometheus.OpenMetricsFormatter;
import org.eclipse.microprofile.metrics.ConcurrentGauge;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metered;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.SimpleTimer;
import org.eclipse.microprofile.metrics.Snapshot;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.Timer;

public class PrometheusFormatter extends OpenMetricsFormatter {

    public StringBuilder toText(MetricRegistry registry, String registryKey, Map<String, Metric> entries) {
        Map<String, Metadata> metadatas = registry.getMetadata();
        Map<Metric, MetricID> ids = registry.getMetrics().entrySet().stream()
            .collect(toMap(Map.Entry::getValue, Map.Entry::getKey));
        return entries.entrySet().stream()
            .map(it -> {
                String key = it.getKey();
                int tagSep = key.indexOf(';');
                if (tagSep > 0) {
                    key = key.substring(0, tagSep);
                }
                Metadata metadata = metadatas.get(key);
                return new Entry(metadata, registryKey + '_' + toPrometheusKey(metadata), it.getValue(), ids.get(it.getValue()));
            })
            .filter(it -> prefixFilter == null || prefixFilter.test(it.prometheusKey))
            .map(entry -> {
                List<Tag> tagsAsList = getTags(entry);
                switch (entry.metadata.getTypeRaw()) {
                    case COUNTER:
                        return counterToText(registryKey, entry, tagsAsList);
                    case CONCURRENT_GAUGE:
                        return concurrentGaugeToText(registryKey, entry, tagsAsList);
                    case GAUGE:
                        return gaugeToText(registryKey, entry, tagsAsList);
                    case METERED:
                        return meteredToText(registryKey, entry, tagsAsList);
                    case TIMER:
                        return timeToText(registryKey, entry, tagsAsList);
                    case SIMPLE_TIMER:
                        return simpleTimeToText(registryKey, entry, tagsAsList);
                    case HISTOGRAM:
                        return histogramToText(registryKey, entry, tagsAsList);
                    default:
                        return new StringBuilder();
                }
            })
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }

    private StringBuilder counterToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        String key = toPrometheusKey(entry.metadata);
        if (!key.endsWith("_total")) {
            key += "_total";
        }
        return counter(registryKey, entry, tagsAsList, key);
    }

    private StringBuilder concurrentGaugeToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        String key = toPrometheusKey(entry.metadata);
        ConcurrentGauge concurrentGauge = ConcurrentGauge.class.cast(entry.metric);
        return concurrentGauge(registryKey, entry, tagsAsList, key, concurrentGauge);
    }

    private StringBuilder gaugeToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        Object value = Gauge.class.cast(entry.metric).getValue();
        if (Number.class.isInstance(value)) {
            String key = toPrometheusKey(entry.metadata);
            return gauge(registryKey, entry, tagsAsList, Number.class.cast(value), key);
        }
        return new StringBuilder();
    }

    private StringBuilder meteredToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        Meter meter = Meter.class.cast(entry.metric);
        String keyBase = toPrometheus(entry.metadata);
        return meter(registryKey, entry, tagsAsList, meter, keyBase);
    }

    private StringBuilder timeToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        String keyBase = toPrometheus(entry.metadata);
        String keyUnit = toUnitSuffix(entry.metadata, false);
        Timer timer = Timer.class.cast(entry.metric);
        return timer(registryKey, entry, tagsAsList, keyBase, keyUnit, timer);
    }

    private StringBuilder simpleTimeToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        String keyBase = toPrometheus(entry.metadata);
        String keyUnit = toUnitSuffix(entry.metadata, false);
        SimpleTimer timer = SimpleTimer.class.cast(entry.metric);
        return simpleTimer(registryKey, entry, tagsAsList, keyBase, keyUnit, timer);
    }

    private StringBuilder histogramToText(String registryKey, Entry entry, List<Tag> tagsAsList) {
        String keyBase = toPrometheus(entry.metadata);
        String keyUnit = toUnitSuffix(entry.metadata, false);
        Histogram histogram = Histogram.class.cast(entry.metric);
        return histogram(registryKey, entry, tagsAsList, keyBase, keyUnit, histogram);
    }

    private List<Tag> getTags(final Entry entry) {
        return globalTags == null || globalTags.length == 0
            ? entry.metricId.getTagsAsList()
            : Stream.concat(entry.metricId.getTagsAsList().stream(), Stream.of(globalTags))
                .distinct()
                .collect(toList());
    }

    private StringBuilder histogram(String key, Entry entry, List<Tag> tagsAsList, String keyBase, String keyUnit, Histogram histogram) {
        final String type = entry.metadata == null ? null : entry.metadata.getType();
        return new StringBuilder()
                .append(type(key, keyBase + keyUnit, "summary"))
                .append(value(key, keyBase + keyUnit + "_count", histogram.getCount(), type, entry.metadata, tagsAsList))
                .append(value(key, keyBase + keyUnit + "_sum",
                    stream(histogram.getSnapshot().getValues()).sum(), type, entry.metadata, tagsAsList))
                .append(toPrometheus(key, keyBase, keyUnit, histogram.getSnapshot(), entry.metadata, tagsAsList));
    }

    private StringBuilder timer(String registryKey, Entry entry, List<Tag> tagsAsList, String keyBase, String keyUnit, Timer timer) {
        final Duration elapsedTime = timer.getElapsedTime();
        final String type = entry.metadata == null ? null : entry.metadata.getType();
        return new StringBuilder()
                .append(type(registryKey, keyBase + keyUnit, "summary"))
                .append(value(registryKey, keyBase + keyUnit + "_count", timer.getCount(), type, entry.metadata, tagsAsList))
                .append(value(registryKey, keyBase + "_elapsedTime",
                    elapsedTime == null ? 0 : elapsedTime.toNanos(), type, entry.metadata, tagsAsList))
                .append(meter(registryKey, entry, tagsAsList, timer, keyBase))
                .append(toPrometheus(registryKey, keyBase, keyUnit, timer.getSnapshot(), entry.metadata, tagsAsList));
    }

    private StringBuilder simpleTimer(String key, Entry entry, List<Tag> tagsAsList, String keyBase, String keyUnit, SimpleTimer timer) {
        final Duration elapsedTime = timer.getElapsedTime();
        final StringBuilder builder = new StringBuilder()
            .append(type(key, keyBase + keyUnit + " summary", "simple timer"))
            .append(value(key, keyBase + "_total", timer.getCount(), "counter", entry.metadata, tagsAsList))
            .append(value(key, keyBase + "_elapsedTime" + keyUnit,
            elapsedTime == null ? 0 : elapsedTime.toNanos(), "simple timer", entry.metadata, tagsAsList));
        final Duration minTimeDuration = timer.getMinTimeDuration();
        builder.append(value(key, keyBase + "_minTimeDuration" + keyUnit,
            minTimeDuration == null ? Double.NaN : minTimeDuration.toNanos(), "simple timer", entry.metadata, tagsAsList));
        final Duration maxTimeDuration = timer.getMaxTimeDuration();
        builder.append(value(key, keyBase + "_maxTimeDuration" + keyUnit,
            maxTimeDuration == null ? Double.NaN : maxTimeDuration.toNanos(), "simple timer", entry.metadata, tagsAsList));
        return builder;
    }

    private StringBuilder meter(String key, Entry entry, List<Tag> tagsAsList, Metered meter, String base) {
        final String type = entry.metadata == null ? null : entry.metadata.getType();
        return new StringBuilder()
                .append(value(key, base + "_rate_per_second", meter.getMeanRate(), type, entry.metadata, tagsAsList))
                .append(value(key, base + "_one_min_rate_per_second", meter.getOneMinuteRate(), type, entry.metadata, tagsAsList))
                .append(value(key, base + "_five_min_rate_per_second", meter.getFiveMinuteRate(), type, entry.metadata, tagsAsList))
                .append(value(key, base + "_fifteen_min_rate_per_second", meter.getFifteenMinuteRate(), type, entry.metadata, tagsAsList))
                .append(value(key, base + "_total", meter.getCount(), type, entry.metadata, tagsAsList));
    }

    private StringBuilder gauge(String registryKey, Entry entry, List<Tag> tagsAsList, Number value, String key) {
        return new StringBuilder().append(value(
            registryKey, key, value.doubleValue(), entry.metadata == null ? null : entry.metadata.getType(), entry.metadata, tagsAsList));
    }

    private StringBuilder concurrentGauge(String registryKey, Entry entry, List<Tag> tagsAsList, String key, ConcurrentGauge gauge) {
        final String type = entry.metadata == null ? null : entry.metadata.getType();
        return new StringBuilder()
                .append(value(registryKey, key + "_current", gauge.getCount(), type, entry.metadata, tagsAsList))
                .append(value(registryKey, key + "_min", gauge.getMin(), type, entry.metadata, tagsAsList))
                .append(value(registryKey, key + "_max", gauge.getMax(), type, entry.metadata, tagsAsList));
    }

    private StringBuilder counter(final String registryKey, final Entry entry, final List<Tag> tagsAsList, final String key) {
        return new StringBuilder()
                .append(value(registryKey, key, Counter.class.cast(entry.metric).getCount(),
                        entry.metadata == null ? null : entry.metadata.getType(), entry.metadata, tagsAsList));
    }

    private StringBuilder toPrometheus(String registryKey, String keyBase, String keyUnit,
        Snapshot snapshot, Metadata metadata, Collection<Tag> tags) {

        final Function<Stream<Tag>, Collection<Tag>> metaFactory = newTags -> Stream.concat(
            tags == null ? Stream.empty() : tags.stream(), newTags).distinct().collect(toList());
        final String completeKey = keyBase + keyUnit;
        final String type = metadata == null ? null : metadata.getType();
        return new StringBuilder()
                .append(value(registryKey, keyBase + "_min" + keyUnit, snapshot.getMin(), type, metadata, tags))
                .append(value(registryKey, keyBase + "_max" + keyUnit, snapshot.getMax(), type, metadata, tags))
                .append(value(registryKey, keyBase + "_mean" + keyUnit, snapshot.getMean(), type, metadata, tags))
                .append(value(registryKey, keyBase + "_stddev" + keyUnit, snapshot.getStdDev(), type, metadata, tags))
                .append(value(registryKey, completeKey, snapshot.getMedian(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.5")))))
                .append(value(registryKey, completeKey, snapshot.get75thPercentile(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.75")))))
                .append(value(registryKey, completeKey, snapshot.get95thPercentile(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.95")))))
                .append(value(registryKey, completeKey, snapshot.get98thPercentile(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.98")))))
                .append(value(registryKey, completeKey, snapshot.get99thPercentile(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.99")))))
                .append(value(registryKey, completeKey, snapshot.get999thPercentile(), type, metadata,
                        metaFactory.apply(Stream.of(new Tag("quantile", "0.999")))));
    }

    private String toPrometheusKey(final Metadata metadata) {
        return toPrometheus(metadata) + toUnitSuffix(metadata, metadata.getTypeRaw() == MetricType.COUNTER);
    }

    private String toUnitSuffix(final Metadata metadata, final boolean enforceValid) {
        final String unit = enforceValid ? getValidUnit(metadata) : (metadata.getUnit() == null ? MetricUnits.NONE : metadata.getUnit());
        return MetricUnits.NONE.equalsIgnoreCase(unit)
            || (enforceValid && !validUnits.contains(unit)) ? "" : ("_" + toPrometheusUnit(unit));
    }

    private StringBuilder value(String registryKey, String key, double value, String type, Metadata metadata, Collection<Tag> tags) {
        final String builtKey = registryKey + '_' + key;
        return new StringBuilder()
                .append(type(registryKey, key, type))
                .append(keyMapping.getOrDefault(builtKey, builtKey))
                .append(of(tags)
                        .filter(t -> !t.isEmpty())
                        .map(t -> tags.stream()
                                .map(e -> e.getTagName() + "=\"" + e.getTagValue() + "\"")
                                .collect(joining(",", "{", "}")))
                        .orElse(""))
                .append(' ').append(new BigDecimal(toPrometheusValue(getValidUnit(metadata), value)).toPlainString()).append("\n");
    }

    private String getValidUnit(final Metadata metadata) {
        final String unit = metadata.getUnit() == null ? MetricUnits.NONE : metadata.getUnit();
        // for tck, we dont really want to prevent the user to add new units
        // we should likely just check it exists in MetricUnits constant but it is too restrictive
        if (unit.startsWith("jelly")) {
            return MetricUnits.NONE;
        }
        return unit;
    }

    private StringBuilder type(final String registryKey, final String key, final String type) {
        final String builtKey = registryKey + '_' + key;
        final StringBuilder builder = new StringBuilder()
            .append("# TYPE ").append(keyMapping.getOrDefault(builtKey, builtKey));
        if (type != null) {
            builder.append(' ').append(type);
        }
        return builder.append("\n");
    }

    private String toPrometheusUnit(final String unit) {
        if (unit == null) {
            return null;
        } else if (unit.endsWith(MetricUnits.BITS) || unit.endsWith(MetricUnits.BYTES)) {
            return MetricUnits.BYTES;
        } else if (unit.endsWith(MetricUnits.SECONDS)
            || unit.equals(MetricUnits.MINUTES)
            || unit.equals(MetricUnits.HOURS)
            || unit.equals(MetricUnits.DAYS)) {

            return MetricUnits.SECONDS;
        } else {
            return unit;
        }
    }

    private double toPrometheusValue(String unit, double value) {
        if (unit == null) {
            return value;
        } else if (unit.endsWith(MetricUnits.BITS)) {
            return bitsToBytes(unit, value);
        } else if (unit.endsWith(MetricUnits.BYTES)) {
            return toBytes(unit, value);
        } else if (unit.endsWith(MetricUnits.SECONDS)) {
            return toSeconds(unit, value);
        }
        switch (unit) {
            case MetricUnits.MINUTES: return value * 60 / pow(1000, 3);
            case MetricUnits.HOURS: return value * pow(60, 2) / pow(1000, 3);
            case MetricUnits.DAYS: return value * pow(60, 2) * 24 / pow(1000, 3);
            default: return value;
        }
    }

    private double bitsToBytes(String unit, double value) {
        switch (unit) {
            case MetricUnits.BITS: return value / 8;
            case MetricUnits.KILOBITS: return value * 1000 / 8;
            case MetricUnits.MEGABITS: return value * pow(1000, 2) / 8;
            case MetricUnits.GIGABITS: return value * pow(1000, 3) / 8;
            case MetricUnits.KIBIBITS: return value * 128;
            case MetricUnits.MEBIBITS: return value * pow(1024, 2);
            case MetricUnits.GIBIBITS: return value * pow(1024, 3);
            default: return value;
        }
    }

    private double toBytes(String unit, double value) {
        switch (unit) {
            case MetricUnits.KILOBYTES: return value * 1000;
            case MetricUnits.MEGABYTES: return value * pow(1000, 2);
            case MetricUnits.GIGABYTES: return value * pow(1000, 3);
            default: return value;
        }
    }

    private double toSeconds(String unit, double value) {
        switch (unit) {
            case MetricUnits.NANOSECONDS: return value;
            case MetricUnits.MICROSECONDS: return value / 1000;
            case MetricUnits.MILLISECONDS: return value / pow(1000, 2);
            default: return value / pow(1000, 3);
        }
    }

    private String toPrometheus(final Metadata id) {
        return id.getName()
                .replaceAll("[^\\w]+", "_")
                .replace("__", "_")
                .replace(":_", ":");
    }

    private static class Entry {
        private final Metadata metadata;
        private final String prometheusKey;
        private final Metric metric;
        private final MetricID metricId;

        private Entry(Metadata metadata, String prometheusKey, Metric metric, MetricID metricId) {
            this.metadata = metadata;
            this.prometheusKey = prometheusKey;
            this.metric = metric;
            this.metricId = metricId;
        }
    }
}
