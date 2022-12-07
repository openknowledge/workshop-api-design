set -B # enable brace expansion
while true
do
curl --location --request PUT 'http://localhost:30082/customers/0815/delivery-address' \
--header 'Content-Type: application/json' \
--data-raw '{
    "city": "26121 Oldenburg (Oldenburg)",
    "recipient": "Max Mustermann",
    "street": {
        "name": "Poststraße",
        "number": "1"
    }
}'
echo 'PUT request for delivery address finished.\n'
sleep 0.5
done