'user strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change, context) => {
  const user_id = context.params.user_id;
  const notification_id = context.params.notificaiton_id;

  console.log('Notification sent to user id: ', user_id);

  const from_user_id = change.after.val().from;

  const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
  return deviceToken.then(result => {

    const token_id = result.val();

    const payload = {
      notification: {
        title: "New Message",
        body: "You've received a new Message",
        icon: "default"
      },
      data: {
        from_user_id: from_user_id
      }
    };

    return admin.messaging().sendToDevice(token_id, payload).then(respons => {

      return console.log('This was the notification feature');
    });

  });


});
