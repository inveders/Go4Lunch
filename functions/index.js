	
const admin = require('firebase-admin');
const functions = require('firebase-functions');

admin.initializeApp({
	credential: admin.credential.applicationDefault(),
	databaseURL: 'https://go4lunch-inved.firebaseio.com'
  });

//admin.initializeApp();

const db = admin.firestore();
	
	exports.sendNotificationsAtLunchTime = functions.https.onRequest((req,res)=>
	{

		console.log('We are in sendNotificationAtLunchTimeFunction:');
		var userId;	
		listAllUsers();

		function listAllUsers(nextPageToken) {
			// List batch of users, 1000 at a time.
			admin.auth().listUsers(1000, nextPageToken)
			  .then(function(listUsersResult) {
				listUsersResult.users.forEach(function(userRecord) {
				  console.log('user', userRecord.uid);
				  

						//RETRIEVE USER INFORMATIONS ON HIS LUNCH
						db.collectionGroup('users').where('uid','==',userRecord.uid).get().then(function(querySnapshot) {
							querySnapshot.forEach(function(doc) {
								console.log(doc.id, ' => restaurantName ', doc.data().restaurantName);
								var restaurantName=doc.data().restaurantName;
								var myFirstname=doc.data().firstname;
								var restaurantAddress=doc.data().restaurantVicinity;
								var restaurantPlaceId=doc.data().restaurantPlaceId;
								var jobPlaceId=doc.data().jobPlaceId;								
								var registrationToken = doc.data().token;
								var isNotificationEnabled = doc.data().notificationEnabled;

									//RETRIEVE WORKMATES
									db.collection('location').doc(jobPlaceId).collection('users').where('restaurantPlaceId', '==', restaurantPlaceId).limit(15).get().then(snapshot => {
										
										const workmates=[];
										
										snapshot.forEach(doc => {
											workmates.push(doc.data().firstname);
										});

										const indexWorkmates = workmates.indexOf(myFirstname);
									
										if (indexWorkmates > -1) {
										workmates.splice(indexWorkmates, 1);
										}
										console.log('Workmates lenght:', workmates.length);

										if(workmates.length===0){
											console.log('Workmates is null:', workmates);
											workmates.push("personne pour le moment");
										}
							
										//SEND NOTIFICATION					
										var titleValFrench = 'Votre repas ce midi';
										var textValFrench = 'Ce midi vous mangez au restaurant '+restaurantName+' situé '+restaurantAddress+ ' avec '+workmates+'.';
							
										var payloadFrench = {
											notification: {
												title: titleValFrench,
												body: textValFrench
											},
											token:registrationToken
										};		
										

										if(restaurantName===null){
											console.log('Restaurant name is null:', restaurantName);
											res.status(200).send("Your Lunch");
										}
										else{
											if(isNotificationEnabled){
												admin.messaging().send(payloadFrench).then((response) => {
													// Response is a message ID string.
													console.log('Successfully sent message:', response);
													res.status(200).send("Your Lunch");
												})
												.catch((error) => {
													console.log('Error sending message:', error);
												});	
											}
											else{
												console.log('Notification is not enabled', isNotificationEnabled);
											}
											
										}
																														
										
									})
									.catch(err => {
										console.log('Error getting workmates documents', err);
									});
								
							});
						})
						.catch(err => {
							console.log('Error getting document', err);
						});





				
				});

			 	if (listUsersResult.pageToken) {
				  // List next batch of users.
				  listAllUsers(listUsersResult.pageToken);
				}

				//ICI ON DEVRAIT METTRE LE RES.SEND du http request
			  })
			  .catch(function(error) {
				console.log('Error listing users:', error);
			  });
		  }

				

});