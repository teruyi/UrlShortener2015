'use strict';


angular.module('urlshortenerApp')

.controller('StatsController', ['$http','UserService','$routeParams','$window','$location','$filter', function ($http,UserService,$routeParams,$window,$location,$filter) {


  var self = this;
  self.hash = $routeParams.hash;

  self.startDateActivated = false;
  self.endDateActivated = false;

  self.startDate = undefined;
  self.endDate = undefined;


  self.stats = [];
  self.stats['city'] = {};
  self.stats['country'] = {};
  self.stats['region'] = {};


  self.alert = [];

  self.alert['city'] =  {type:"none"}; //Alert
  self.alert['country'] =  {type:"none"}; //Alert
  self.alert['region'] =  {type:"none"}; //Alert
  self.alert['general'] =  {type:"none"}; //Alert




  self.loadData = function(type){
    //Get from server


    var parameters = 'type=' + type;
    if(self.startDateActivated){
      parameters += '&start=' +  $filter('date')(self.startDate, "yyyy/MM/dd");
    }
    if(self.endDateActivated){
      parameters += '&end=' + $filter('date')(self.endDate, "yyyy/MM/dd");
    }
    if(self.hash){
      parameters += '&id=' + self.hash;
    }

    $http.get('/info?' + parameters).
    success(function(data, status, headers, config) {
      //Success
      //We display info

      var current = data.data;

      //Clear data
      self.stats[type].labels = [];
      self.stats[type].data = [];

      self.alert[type].type = "success";

      //Format data for chart
      for(var i = 0; i < current.length; i++){
        var c = current[i];
        self.stats[type].labels.push(c.name);
        self.stats[type].data.push(c.number);
      }


      if(current.length==0){
        self.alert[type].type = "error";
        self.alert[type].title = "No data available";
      }



    }).
    error(function(data, status, headers, config) {
      //Error
      //We display error

      if(status=='403'){
        self.alert['general'].type = "error";
        self.alert['general'].title = "Error 403";
        self.alert['general'].message = "You don't have permission";

      }
      else if(status=='404'){
        self.alert['general'].type = "error";
        self.alert['general'].title = "Error 404";
        self.alert['general'].message = "Link '" + self.hash + "' does not exist";

      }
    
    });


  };

  self.loadAllData = function(){
    if(self.startDateActivated && !self.startDate){
      alert("Please, enter a start date or uncheck it");
      return;
    }
    if(self.endDateActivated && !self.endDate){
      alert("Please, enter a end date or uncheck it");
      return;
    }
    if(self.endDateActivated && self.startDateActivated){
      if(self.endDate < self.startDate){
        alert("Start date cannot be higher than end date");
        return;
      }
    }

    self.loadData('city');
    self.loadData('region');
    self.loadData('country');
  };


  self.loadAllData();

}]);
