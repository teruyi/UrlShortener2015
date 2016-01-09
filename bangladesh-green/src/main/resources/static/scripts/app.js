'use strict';

/**
* @ngdoc overview
* @name urlshortenerApp
* @description
* # urlshortenerApp
*
* Main module of the application.
*/
angular
.module('urlshortenerApp', [
  'ngRoute','angular-jwt','ngStorage','ui.checkbox','ngCookies','chart.js'
])
.config(function ($routeProvider,$httpProvider) {

  $httpProvider.defaults.withCredentials = true;
  //This function checks if the user is logged-in
  //and redirects to login if its not.
  var onlyLoggedIn = function ($location,$q,UserService) {
    var deferred = $q.defer();
    if (UserService.currentlyLogged) {
      deferred.resolve();
    } else {
      deferred.reject();
      $location.url('/login');
    }
    return deferred.promise;
  };

  //This function checks if the user is logged-in
  //and redirects to main if it is.
  var onlyNotLoggedIn = function ($location,$q,UserService) {
    var deferred = $q.defer();
    if (!UserService.currentlyLogged) {
      deferred.resolve();
    } else {
      deferred.reject();
      $location.url('/shorten');
    }
    return deferred.promise;
  };




  $routeProvider
  .when('/shorten', {
    templateUrl: 'views/main.html',
    controller: 'MainController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyLoggedIn} //Only for logged-in users
  })

  .when('/login', {
    templateUrl: 'views/login.html',
    controller: 'RegisterController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyNotLoggedIn} //Only for NOT logged-in users
  })

  .when('/user/:username',{
    templateUrl: 'views/user.html',
    controller: 'UserController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyLoggedIn} //Only for logged-in users
  })


  .when('/admin', {
    templateUrl: 'views/admin.html',
    controller: 'AdminController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyLoggedIn} //Only for logged-in users
  })

  .when('/systeminfo', {
    templateUrl: 'views/system.html',
    controller: 'SystemController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyLoggedIn} //Only for logged-in users
  })

  .when('/stats/:hash?', {
    templateUrl: 'views/stats.html',
    controller: 'StatsController',
    controllerAs: 'ctrl',
    resolve:{loggedIn:onlyLoggedIn} //Only for logged-in users
  })

  .when('/bridge/:hash/:logout?',{
    templateUrl: 'views/bridgeLogin.html',
    controller: 'BridgeController',
    controllerAs: 'ctrl'
  })


  .otherwise({ //Default -> go to shorten page
    redirectTo: '/shorten'
  });

  //HTTP Interceptor.
  //On every request, it sends authorization token (if present).
  //On every response, if STATUS CODE is 401 unauthorized (need authentication) redirects to login.
  $httpProvider.interceptors.push(['$q', '$location', '$localStorage', function ($q, $location, $localStorage) {
    return {
      response: function(response){

        if (response.status === 401 && $location.path().indexOf("bridge") == -1) {
          $location.path('/login');
        }
        return response || $q.when(response);
      },
      responseError: function(rejection) {
        console.log(rejection);
        if (rejection.status === 401 && $location.path().indexOf("bridge") == -1) {
          $location.path('/login');
        }
        return $q.reject(rejection);
      }
    };

  }]);
});
