'use strict';


angular.module('urlshortenerApp')

.controller('BridgeController', ['$http','UserService','$location','$window','$routeParams','$cookies',function ($http,UserService,$location,$window,$routeParams,$cookies) {



  var self = this;
  self.visible = false;

  self.hash = $routeParams.hash;
  self.logout = ($routeParams.logout=='logout');
  self.mode = "login"; //Mode, can be "login" or "register"
  self.alert = {type:"none"}; //Alert
  self.newUser= {}; //Used for user registration
  self.loginUser={}; //Used for user log-in
  self.rememberLogin = true;

  self.resetAlert = function(){
    self.alert.type = "none";
    self.alert.title = "";
    self.alert.text = "";
  };


  //Resets user errors
  self.resetUserErrors = function(){
    self.userErrors = {
      username: {
        error: false,
        message: ""
      },
      email: {
        error: false,
        message:  ""
      },
      password: {
        error: false,
        message:  ""
      },
      rePassword: {
        error: false,
        message:  ""
      },
      realName: {
        error: false,
        message:  ""
      }

    }
  };



  self.loginUserDataIsValid = function(){

    var hasErrors = false;

    self.resetUserErrors();

    if(!self.loginUser || !self.loginUser.username || self.loginUser.username.length == 0){
      self.userErrors.username.error = true;
      self.userErrors.username.message = "Please specify a username";
      hasErrors = true;
    }

    if(!self.loginUser || !self.loginUser.password || self.loginUser.password.length == 0){
      self.userErrors.password.error = true;
      self.userErrors.password.message = "Please specify a password";
      hasErrors = true;
    }

    return !hasErrors;
  };

  self.doLogin = function(){

    self.resetAlert();

    //Only if data can be valid
    if(self.loginUserDataIsValid()){
      $http.post('/login', self.loginUser)
      .then(function(message){


        self.loginUser = {};          //Clear new user
        self.resetUserErrors();     //Clear user errors
        self.mode='login';          //Go to login mode

        self.token = $cookies.get("wallaclaim");
      
        //Set received token
        UserService.setNewToken($cookies.get("wallaclaim"),self.rememberLogin);


        //Go to private LINK
        $window.location.href = self.hash;


      })
      .catch(function(message){
        //Error (!2XX code)
        self.alert.type = "error";

        self.alert.title ="Error while logging-in";
        if(message.data.message){
          self.alert.message= message.data.message;
        }
        else{
          self.alert.message = "Error " + message.status;
        }


      });

    }
  };


  //Do every initialization
  self.resetUserErrors();


  //First, logout if requested
  if(self.logout){
    UserService.deleteCurrentToken();

  };


  //If user is loged-in, redirect
  if(UserService.currentlyLogged){
    $window.location.href = self.hash;
  }
  else{
    self.visible = true;
  };



}]);
