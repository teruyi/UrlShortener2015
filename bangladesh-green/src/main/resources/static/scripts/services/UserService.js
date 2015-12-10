'use strict';


angular.module('urlshortenerApp')

    .service('UserService', function (jwtHelper,$localStorage) {


        var self = this;

        self.currentlyLogged = false; //Indicates if we have information about a token or not

        self.username = undefined;  //Current user username
        self.roles= undefined;      //Current user roles
        self.token=undefined;       //Current token in text
        self.claims= undefined;     //Curent token's claims


        /**
         * Sets new token from "newToken" from String.
         * if "saveOnLocalStorage", saves it on localStorage for future uses.
         */
        self.setNewToken = function(newToken,saveOnLocalStorage){

            self.token = newToken;
            self.claims = jwtHelper.decodeToken(self.token);
            self.username = self.claims.sub;
            self.roles = self.claims.roles;

            self.currentlyLogged = true; //Set flag to TRUE

            console.log("[LOGIN] Obtained token for  " + self.username);

            if(saveOnLocalStorage){
                //Save token on LocalStorage if requested
                $localStorage.token = self.token;
                console.log("[LOGIN] Saved token on localStorage");
            }

        };


        /*
         Loads token from local storage and returns true.
         If does not exists, returns false;
         */
        self.loadFromLocalStorage = function(){
            if($localStorage.token){
                self.setNewToken($localStorage.token);
                return true;
            }
            return false;
        }



        /**
         * Deletes all data of token from memory and localStorage.
         */
        self.deleteCurrentToken = function(){

            //Delete from localStorage
            $localStorage.token = undefined;

            //Delete from memory
            self.username = undefined;
            self.roles= undefined;
            self.token=undefined;
            self.claims= undefined;

            //Set flag to false
            self.currentlyLogged = false;
        };


        //At start, load from local storage
        self.loadFromLocalStorage();


    });
