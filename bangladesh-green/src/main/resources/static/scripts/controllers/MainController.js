'use strict';


angular.module('urlshortenerApp')

.controller('MainController', ['$http','UserService',function ($http,UserService) {




  var self = this;


  //Current URL object
  self.url = {
    target: undefined,
    privateURI: false
  };

  self.url.authorizedUsers = [UserService.username];

  //Current result object
  self.result = undefined;


  self.items = [
    { id: -1, name: 'Never' },
    { id: 60, name: '1 min' },
    { id: 300, name: '5 min' },
    { id: 900, name: '15 min' },
    { id: 1800, name: '30 min' },
    { id: 3600, name: '1 hour' },
    { id: 18000, name: '5 hour' },
    { id: 43200, name: '12 hour' },
    { id: 86400, name: '1 day' },
    { id: 604800, name: '1 week' },
    { id: 2592000, name: '1 month' },
    { id: 31536000, name: '1 year' },
  ];


  self.deleteAuthorizedUser = function(index){
    if (index > -1) {
      self.url.authorizedUsers.splice(index, 1);
    }
  };


  self.addAuthorizedUser = function(){
    if(!self.url.authorizedUsers){
      self.url.authorizedUsers = [];
    }

    if(self.newUser && self.newUser.length > 0){
      self.url.authorizedUsers.push(self.newUser);
      self.newUser = "";
    }
  };

  self.selectedItem = null;

  //Short a link
  self.doShort = function(){

    if(self.expirationVisible && self.selectedExpiration && self.selectedExpiration.id >=0){
      //Set expiration date
      self.url.expirationSeconds = self.selectedExpiration.id;
    }

    if(!self.userListVisible){
      self.url.authorizedUsers = undefined;
    }

    //Add http if not present
    if(self.url.target.indexOf("http://")!=0 && self.url.target.indexOf("https://")!=0){
      self.url.target = "http://" + self.url.target;
    }

    $http.post('/link', self.url)
    .then(function(message){
      //Success
      self.result = message.data;
    })
    .catch(function(message){
      //Error (!2XX code)
      self.result = message.data;
    });

  };

}]);
