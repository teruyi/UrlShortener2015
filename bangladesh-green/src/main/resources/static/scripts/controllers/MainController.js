'use strict';


angular.module('urlshortenerApp')

  .controller('MainController', function ($http) {


    var self = this;

	  //Current URL object
    self.url = {
    	target: undefined,
    	privateURI: false,
		authorizedUsers: ["ismaro3"]
    };

	  //Current result object
	  self.result = undefined;

	  //Short a link
    self.doShort = function(){

    	$http.post('/link', self.url)
    	.then(function(message){
			//Success
			self.result = message.data;
    	})
    	.catch(function(message){
			//Error (!2XX code)
    		self.result = message.data;
    	});

    }

  });
