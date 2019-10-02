	
const admin = require('firebase-admin');
const functions = require('firebase-functions');

admin.initializeApp(functions.config().firebase);
/*admin.initializeApp({
    credential: admin.credential.applicationDefault(),
    databaseURL: "https://go4lunch-inved.firebaseio.com"
});*/

const db = admin.firestore();
	
	exports.sendNotificationsAtLunchTime = functions.https.onRequest((req,res)=>
	{

		res.status(200).send("Your Lunch");

		
		//INITIALIZE VARIABLES
	/*	var restaurantName = 'bateau';
		var restaurantAddress = null;
		var restaurantPlaceId = null;
		var jobPlaceId = null;
		var workmates = [];*/
		//var db = admin.firestore();

		//var restaurantName='MyFriend';
				

				

		//RETRIEVE USER INFORMATIONS ON HIS LUNCH
		let getUserInfo = db.collectionGroup('users').where('uid','==','wu8M8NfwxrWp4VQaCY410GW2bzk1').get().then(function(querySnapshot) {
			querySnapshot.forEach(function(doc) {
				console.log(doc.id, ' => restaurantName ', doc.data().restaurantName);
				var restaurantName=doc.data().restaurantName;
				var restaurantAddress=doc.data().restaurantVicinity;
				var restaurantPlaceId=doc.data().restaurantPlaceId;
				var jobPlaceId=doc.data().jobPlaceId;

					//RETRIEVE WORKMATES
					let getWorkmatesList = db.collection('location').doc(jobPlaceId).collection('users');
					let query = getWorkmatesList.where('restaurantPlaceId', '==', restaurantPlaceId).limit(15).get().then(snapshot => {
						
						const workmates=[];
						
						snapshot.forEach(doc => {
							workmates.push(doc.data().firstname);
						});


						//************************** */
						//SEND UNKNOWN NOTIFICATION					
						var titleValFrencha = 'Votre repas ce midi ci';
						var textValFrencha = 'Ce midi vous mangez au restaurant '+restaurantName+' situé '+restaurantAddress+ ' avec '+workmates;
						var topicFrencha='go4lunchFrench';

						var payloadFrencha = {
							notification: {
								title: titleValFrencha,
								body: textValFrencha
							},
							topic:topicFrencha
						};		
						
						admin.messaging().send(payloadFrencha)
						.then((response) => {
							// Response is a message ID string.
							console.log('Successfully sent message:', response);
						})
						.catch((error) => {
							console.log('Error sending message:', error);
						});
						//************************** */
						//return workmates; //or return "" as before
					})
					.catch(err => {
						console.log('Error getting workmates documents', err);
					});


				
			});
		})
		.catch(err => {
			console.log('Error getting document', err);
		});

		return'';

	/*	

		


		/*function listAllUsers(nextPageToken) {
		  // List batch of users, 1000 at a time.
		  admin.auth().listUsers(1000, nextPageToken)
		    .then(function(listUsersResult) {
		      listUsersResult.users.forEach(function(userRecord) {
		        var userId = userRecord.Uid;
		        console.log('user JSON', userRecord.toJSON()+' and userId Value is '+userId);
		        

				    




			  });
		      if (listUsersResult.pageToken) {
		        // List next batch of users.
		        listAllUsers(listUsersResult.pageToken);
		      }
		    })
		    .catch(function(error) {
		      console.log('Error listing users:', error);
		    });
		}*/
			

});