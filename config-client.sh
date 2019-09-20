echo "Enter flight-tracker URL [ws://flight-tracker.apps.clearlake.cf-app.com/rsocket]: "
read FLIGHT_TRACKER_URL
FLIGHT_TRACKER_URL=${FLIGHT_TRACKER_URL:-ws://flight-tracker.apps.clearlake.cf-app.com/rsocket}

echo "module.exports = {" > flight-client/src/config.js
echo "    FLIGHT_TRACKER_URL: '${FLIGHT_TRACKER_URL}'" >> flight-client/src/config.js
echo "}" >> flight-client/src/config.js

echo "Successfully set flight-client/src/config.js to"
echo ""
cat flight-client/src/config.js

echo ""
echo "Please re-build flight-tracker before deploying."
