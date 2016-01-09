'use strict';


angular.module('urlshortenerApp')

.service('UserService', ['jwtHelper','$cookies',function (jwtHelper,$cookies) {


  var self = this;

  self.currentlyLogged = false; //Indicates if we have information about a token or not

  self.username = undefined;  //Current user username
  self.roles= undefined;      //Current user roles
  self.token=undefined;       //Current token in text
  self.claims= undefined;     //Curent token's claims


  /**
  * Sets new token from "newToken" from String
  */
  self.setNewToken = function(newToken){

    self.token = newToken;
    self.claims = jwtHelper.decodeToken(self.token);
    self.username = self.claims.sub;
    self.roles = self.claims.roles;

    self.currentlyLogged = true; //Set flag to TRUE

    console.log("[LOGIN] Obtained token for  " + self.username + " from cookie.");



  };


  /*
  Loads token from cookie and returns true.
  If does not exists, returns false;
  */
  self.loadFromCookie = function(){
    if($cookies.get("wallaclaim")){
      self.setNewToken($cookies.get("wallaclaim"));
      return true;
    }
    return false;
  };



  /**
  * Deletes all data of token from memory and cookie
  */
  self.deleteCurrentToken = function(){

    //Delete from cookies
    $cookies.remove("wallaclaim");

    //Delete from memory
    self.username = undefined;
    self.roles= undefined;
    self.token=undefined;
    self.claims= undefined;

    //Set flag to false
    self.currentlyLogged = false;
  };


  //At start, load from local storage
  self.loadFromCookie();


}]);
