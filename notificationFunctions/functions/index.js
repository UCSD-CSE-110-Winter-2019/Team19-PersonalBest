'user strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event => {
  const user_id = event.params.user_id;
  const notification_id = event.params.notificaiton_id;

  console.log('Notification sent to user id: ', user_id);

  if (!event.data.val()) {

    return console.log('Notificaiton deleted');

  }
  const fromUser = admin.database().ref(`/notificaitons/${user_id}/${notificaiton_id}`).once('value');
  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');
    return deviceToken.then(result => {
      
      const token_id = result.val();

      const payload = {
        notificaiton: {
          title: "New Message",
          body: "You've received a new Message",
          icon: "default"
        },
        data: {
          from_user_id: from_user_id
        }
      };

      return admin.messaging().sendToDevice(token_id, payload).then(respons => {

        console.log('This was the notificaiton feature');
      });

    });

  });

});
