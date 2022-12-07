import { CatalogBuilder } from '@backstage/plugin-catalog-backend';
import { ScaffolderEntitiesProcessor } from '@backstage/plugin-scaffolder-backend';
import { Router } from 'express';
import { PluginEnvironment } from '../types';
import {PactBrokerProvider} from "./PactBrokerProvider";

export default async function createPlugin(
  env: PluginEnvironment,
): Promise<Router> {
  const builder = await CatalogBuilder.create(env);
  const pactBrokerProvider = new PactBrokerProvider('production', env.config.get("integrations.pact.url"));
  builder.addEntityProvider(pactBrokerProvider);

  builder.addProcessor(new ScaffolderEntitiesProcessor());
  const { processingEngine, router } = await builder.build();
  await processingEngine.start();

  await env.scheduler.scheduleTask({
    id: 'run_pact_refresh',
    fn: async () => {
      await pactBrokerProvider.run();
    },
    frequency: { seconds: 5 },
    timeout: { seconds: 2 },
  });

  return router;
}
