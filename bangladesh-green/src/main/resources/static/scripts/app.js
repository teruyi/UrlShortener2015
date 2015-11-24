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
    'ngRoute'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl',
        controllerAs: 'ctrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
