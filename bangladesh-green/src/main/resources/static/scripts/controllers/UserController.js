'use strict';


angular.module('urlshortenerApp')


/**
Controller used for viewing user information and editing it
*/
.controller('UserController',['$http','UserService','$routeParams','$window','$location', function ($http,UserService,$routeParams,$window,$location) {

  var self = this;
  self.alert = {type:"none"}; //Alert
  //Given username
  self.username = $routeParams.username;
  self.links = [];
  self.editing = false;
  self.canView = true;

  self.step = 9;
  self.listStart = 0;
  self.listEnd = self.step;

  self.hasMoreLinks = true;

  //Currently logged user roles
  self.currentRoles = UserService.roles;


  self.userBeforeEdit = undefined;

  self.loadInfo = function(){

    //Get from server
    $http.get('/user/' + self.username).
    success(function(data, status, headers, config) {
      //Success
      //We display info

      self.user = data.data;

    }).
    error(function(data, status, headers, config) {
      //Error
      //We display error

      if(status=='403'){
        self.alert.type = "error";
        self.alert.title = "Error 403";
        self.alert.message = "You don't have permission to view this info";
        self.canView = false;
      }
      else if(status=='404'){
        self.alert.type = "error";
        self.alert.title = "Error 404";
        self.alert.message = "User '" + self.username + "' does not exist";
        self.canView = false;
      }

    });
  };


  self.loadLinks = function(){
    //Get from server
    $http.get('/user/' + self.username + "/links?start="+self.listStart+"&end=" + self.listEnd).
    success(function(data, status, headers, config) {
      //Success
      //We display info


      for(var i = 0; i < data.data.length; i++){
        self.links.push(data.data[i]);
      }

      //Increment
      self.listStart = self.listEnd+1;
      self.listEnd= self.listEnd + self.step;

      if(data.data.length < self.step){
        self.hasMoreLinks = false;
      }



    }).
    error(function(data, status, headers, config) {
      //Error
      //We display error

      if(status=='403'){
        self.alert.type = "error";
        self.alert.title = "Error 403";
        self.alert.message = "You don't have permission to view this info";
        self.canView = false;
      }
      else if(status=='404'){
        self.alert.type = "error";
        self.alert.title = "Error 404";
        self.alert.message = "User '" + self.username + "' does not exist";
        self.canView = false;
      }
    
    });


  };


  self.loadInfo();
  self.loadLinks();


  self.resetAlert = function(){
    self.alert.type = "none";
    self.alert.title = "";
    self.alert.text = "";
  };

  self.startEdit = function(){
    self.resetAlert();
    self.userBeforeEdit  = angular.copy(self.user);
    self.editing = true;
  };

  self.discardEdit = function(){
    self.resetAlert();
    self.user =  angular.copy(self.userBeforeEdit);
    self.editing = false;
  };



  self.delete = function(){

    if (confirm('Are you sure you want to delete this account? This can not be undone!')) {

      $http.delete('/user/' + self.username).
      success(function(data, status, headers, config) {

        //If admin, go back
        if(self.currentRoles=='admin'){
          $window.history.back();
        }
        else{
          //If regular user, logout and go to login page
          UserService.deleteCurrentToken();
          $location.path('/login');

        }
      }).
      error(function(message) {
        self.alert.type = "error";
        self.alert.title = "Error";

        if(message.data.message){
          self.alert.message= message.data.message;
        }
        else{
          self.alert.message = "Error " + message.status;
        }
      });

    }

  };

  self.saveEdit = function(){

    self.resetAlert();


    if(!self.user.email || self.user.email.length<=0){
      self.alert.type = "error";
      self.alert.title = "Error";
      self.alert.message="Please, provide a email";
    }
    else if(!self.user.role || self.user.role.length <=0){
      self.alert.type = "error";
      self.alert.title = "Error";
      self.alert.message="Please, provide a role";
    }
    else if(self.user.password != self.user.repassword){
      self.alert.type = "error";
      self.alert.title = "Error";
      self.alert.message="Both passwords must match";
    }
    else{
      $http.put('/user/' + self.username, self.user)
      .then(function(message){
        //Success
        self.alert.type = "success";
        self.alert.title = "Success";
        self.alert.message = "Your data has been updated successfully";

        self.editing = false;


      })
      .catch(function(message){
        //Error (!2XX code)

        self.alert.type = "error";
        self.alert.title = "Error";

        if(message.data.message){
          self.alert.message= message.data.message;
        }
        else{
          self.alert.message = "Error " + message.status;
        }


      });

    }
  };

}]);
