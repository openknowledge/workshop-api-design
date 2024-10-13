echo "Fetching URL for customer-service:"
minikube service -n production customer-service --url
echo ""
echo "Fetching URL for address-validation-service:"
minikube service -n production address-validation-service --url
echo ""
echo "Fetching URL for billing-service:"
minikube service -n production billing-service --url
echo ""
echo "Fetching URL for delivery-service:"
minikube service -n production delivery-service --url
echo ""
echo "Fetching URL for grafana:"
minikube service -n observability kube-prometheus-stack-grafana --url
echo ""
echo "Fetching URL for prometheus:"
minikube service -n observability kube-prometheus-stack-prometheus --url
echo ""
echo "Fetching URL for jaeger:"
minikube service -n observability jaeger-query-nodeport --url
echo ""
