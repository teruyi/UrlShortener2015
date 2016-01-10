'use strict';


angular.module('urlshortenerApp')

  .controller('RegisterController', ['$http','UserService','$location','$cookies',function ($http,UserService,$location,$cookies) {



    var self = this;
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


      self.setLoginMode = function(){
          self.mode="login";
          self.loginUser={};
      };

      self.setRegisterMode = function(){
          self.mode="register";
          self.registerUser={};
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


        //Checks that passwords match
        self.passwordsMatchCheck = function(){
            if(self.newUser && self.newUser.password && self.newUser.rePassword){
                //Passwords exists
                //Only error if first one is filled and not the same as repassword.
                if (self.newUser.password.length > 0 && self.newUser.password !== self.newUser.rePassword){
                    self.userErrors.rePassword.error = true;
                    self.userErrors.rePassword.message = "Passwords do not match";
                    return;
                }
            }

            //No error
             self.userErrors.rePassword.error = false;

        };


    //Returns true if entered data is valid (Not on the server, only on the client)
    self.newUserDataIsValid = function(){

        var hasErrors = false;

        self.resetUserErrors();

        if(!self.newUser || !self.newUser.username || self.newUser.username.length == 0){
            self.userErrors.username.error = true;
            self.userErrors.username.message = "Please specify a username";
            hasErrors = true;
        }
        if(self.newUser && self.newUser.username && self.newUser.username.indexOf('.')>0){
          self.userErrors.username.error = true;
          self.userErrors.username.message = "Username can't contain dots";
          hasErrors = true;
        }

        if(!self.newUser || !self.newUser.email || self.newUser.email.length == 0){
            self.userErrors.email.error = true;
            self.userErrors.email.message = "Please specify a email";
            hasErrors = true;
        }

        if(!self.newUser || !self.newUser.realName || self.newUser.realName.length == 0){
            self.userErrors.realName.error = true;
            self.userErrors.realName.message = "Please specify your real name";
            hasErrors = true;
        }

        if(!self.newUser || !self.newUser.password || self.newUser.password.length == 0){
            self.userErrors.password.error = true;
            self.userErrors.password.message = "Please specify a password";
            hasErrors = true;
        }

        if(!self.newUser || !self.newUser.rePassword || self.newUser.rePassword.length == 0 || self.newUser.rePassword !== self.newUser.password){
            self.userErrors.password.error = true;
            self.userErrors.password.message = "Please repeat your password";
            hasErrors = true;
        }


        return !hasErrors;
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


    //Do the register process
    self.doRegister = function(){

        self.resetAlert();

        //Only if data can be valid
        if(self.newUserDataIsValid()){
            $http.post('/user', self.newUser)
            .then(function(message){
                //Success
                self.alert.type = "success";
                self.alert.title = "User registered";
                self.alert.message = "An email has been sent to you in order to validate your account.";



                self.loginUser.username = self.newUser.username ; //Set login username to the registered
                self.newUser = {};          //Clear new user
                self.resetUserErrors();     //Clear user errors
                self.mode='login';          //Go to login mode


            })
            .catch(function(message){
                //Error (!2XX code)
                self.alert.type = "error";

                self.alert.title ="Can't register user";
                if(message.data.message){
                    self.alert.message= message.data.message;
                }
                else{
                    self.alert.message = "Error " + message.status;
                }


            });
        }

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

                        //Go to shorten
                        $location.path('/shorten');



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



  }]);
