
var eb = new EventBus('/_kotao/eventbus');

eb.onopen = function() {

	// set a handler to receive a message
	eb.registerHandler('updates', function(error, message) {
		console.log('Received a message from Kotao: ' + JSON.stringify(message));
		if (message['body'] === 'finished') {
			location.reload(true);
		}
	});

};
