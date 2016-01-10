'use strict';


angular.module('urlshortenerApp')

.controller('AdminController', ['$http','UserService','$routeParams','$window','$location',function ($http,UserService,$routeParams,$window,$location) {


  var self = this;

  self.userListAlert = {type:"none"}; //
  self.userFilter = {

  };

  self.loadUserList = function(){

    //Get from server the complete user list
    $http.get('/user').
    success(function(data, status, headers, config) {
      //Success
      //We display info

      self.userList = data.data;

    }).
    error(function(data, status, headers, config) {
      //Error
      //We display error

      if(status=='403'){
        self.userListAlert.type = "error";
        self.userListAlert.title = "Error 403";
        self.userListAlert.message = "You don't have permission to view this info";
        self.canView = false;
      }
      else if(status=='404'){
        self.userListAlert.type = "error";
        self.userListAlert.title = "Error 404";
        self.userListAlert.message = "User '" + self.username + "' does not exist";
        self.canView = false;
      }
    
    });
  };

  self.goToProfile = function(username){
    $location.url('/user/' + username);
  };


  self.goToStats = function(){
    $location.url('/stats/');
  };

  self.goToSystem = function(){
    $location.url('/systeminfo/');
  };

  self.loadUserList();

}]);
