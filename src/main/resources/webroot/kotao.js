
var eb = new EventBus('/_kotao/eventbus');

eb.onopen = function() {

	var showSpinner = function () {
		var opts = {
			lines: 9 // The number of lines to draw
			, length: 26 // The length of each line
			, width: 14 // The line thickness
			, radius: 42 // The radius of the inner circle
			, scale: 1.25 // Scales overall size of the spinner
			, corners: 0 // Corner roundness (0..1)
			, color: '#000' // #rgb or #rrggbb or array of colors
			, opacity: 0.25 // Opacity of the lines
			, rotate: 30 // The rotation offset
			, direction: 1 // 1: clockwise, -1: counterclockwise
			, speed: 1 // Rounds per second
			, trail: 60 // Afterglow percentage
			, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
			, zIndex: 2e9 // The z-index (defaults to 2000000000)
			, className: 'spinner' // The CSS class to assign to the spinner
			, top: '50%' // Top position relative to parent
			, left: '50%' // Left position relative to parent
			, shadow: false // Whether to render a shadow
			, hwaccel: false // Whether to use hardware acceleration
			, position: 'absolute' // Element positioning
		};
		var target = document.body;
		var spinner = new Spinner(opts).spin(target);
	};

	// set a handler to receive a message
	eb.registerHandler('updates', function(error, message) {
		console.log('Received a message from Kotao: ' + JSON.stringify(message));
		if (message['body'] === 'starting') {
			showSpinner();
		}
		if (message['body'] === 'finished') {
			location.reload(true);
		}
	});

};
