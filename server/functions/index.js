const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendTripRequestNotification = functions.firestore
  .document("passenger_requests/{passenger}")
  .onWrite((change, context) => {
    // Get the passenger's id
    var passenger = context.params.passenger;

    // Get information
    var payload = {
      data: {
        passenger,
        coordinates: `5.553,-0.123`
      }
    };

    // Send notification to all drivers
    // todo: specify notification target by determining passenger's location from the request
    return admin
      .messaging()
      .sendToTopic("receive_trip_requests", payload)
      .then(() => {
        return console.log("Message sent successfully to all drivers");
      })
      .catch(err => {
        if (err) {
          return console.log(err);
        }
      });
  });
