'use strict';


angular.module('urlshortenerApp')

/**
Controller used for system stats viewing
*/
.controller('SystemController', ['$http','UserService','$routeParams','$window','$location','$filter','$timeout',function ($http,UserService,$routeParams,$window,$location,$filter,$timeout) {


  var self = this;
  self.alert = {type:"none"}; //Alert
  self.canView = true; //Data can be viewed
  self.day =  new Date(); //Current day

  self.cpu = {};
  self.ram = {};
  self.clicks = {};
  self.chartOptions = {pointDot: false, showTooltips: false, animation: false};
  self.clickChartOptions = {animation:false,pointDot:false};


  /* Loads CPU data */
  self.loadCPU = function(){

    //Load total CPU usage
    $http.get('/systeminfo?type=cpu&series=average&day=' + $filter('date')( self.day, "yyyy/MM/dd")).
    then(function(data) {

      //Success
      self.cpu.average = data.data.data;

      return  $http.get('/systeminfo?type=cpu&series=series&day=' + $filter('date')( self.day, "yyyy/MM/dd"));

    }).
    //Load CPU list with 1 point every 30 seconds
    then(function(data){

      var list = data.data.data;
      self.cpu.labels = [];
      self.cpu.data = [[]];
      var pointNumber = 0;
      for(var i = 0; i < list.length; i++){

        if(i%10==0){
          pointNumber++;
          self.cpu.data[0].push(list[i].usage);

          if(pointNumber%(Math.floor(list.length/100))==0){ //Legend only every 10 seconds

            self.cpu.labels.push("" +  $filter('date')( list[i].time, "HH:mm"));
          }
          else{
            self.cpu.labels.push("");
          }
        }
      }
    })

    .catch(function(data) {
      //Error
      if(data.status=='403'){
        self.alert.type = "error";
        self.alert.title = "Error 403";
        self.alert.message = "You don't have permission to view this info";
        self.canView = false;
      }

    });

  };

  /* Loads RAM data */
  self.loadRAM = function(){

    //Load RAM average usage
    $http.get('/systeminfo?type=ram&series=average&day=' + $filter('date')( self.day, "yyyy/MM/dd")).
    then(function(data) {

      //Success
      //We display info
      self.ram.average = data.data.data;

      return  $http.get('/systeminfo?type=ram&series=series&day=' + $filter('date')( self.day, "yyyy/MM/dd"));

    }).
    //Load RAM list
    then(function(data){

      var list = data.data.data;
      self.ram.labels = [];
      self.ram.data = [[]];
      var pointNumber = 0;
      for(var i = 0; i < list.length; i++){

        if(i%10==0){
          pointNumber++;
          self.ram.data[0].push(list[i].usage);

          if(pointNumber%(Math.floor(list.length/100))==0){ //Legend only every 10 seconds

            self.ram.labels.push("" +  $filter('date')( list[i].time, "HH:mm"));
          }
          else{
            self.ram.labels.push("");
          }
        }
      }

    })

    .catch(function(data) {
      //Error
      //We display error
      if(data.status=='403'){
        self.alert.type = "error";
        self.alert.title = "Error 403";
        self.alert.message = "You don't have permission to view this info";
        self.canView = false;
      }


    });

  };


  /* Load click information */
  self.loadClicks = function(){

    //Load total clicks in day
    $http.get('/systeminfo?type=clicks&day=' + $filter('date')( self.day, "yyyy/MM/dd")).
    then(function(data) {

      //Success
      //We display info
      self.clicks.today = data.data.data;

      //Load total clicks all time
      return  $http.get('/systeminfo?type=clicks');

    }).
    then(function(data){
      self.clicks.all = data.data.data;
      //Load click list for day, every hour
      return  $http.get('/systeminfo?type=clicksadds&day='+ $filter('date')( self.day, "yyyy/MM/dd") );
    })

    .then(function(data){

      var list = data.data.data;
      self.clicks.labels = [];
      self.clicks.data = [[]];
      for(var i = 0; i < list.length; i++){


        self.clicks.data[0].push(list[i]);

        self.clicks.labels.push(i + ":00");



      }

    })

    .catch(function(data) {
      //Error
      //We display error
      if(data.status=='403'){
        self.alert.type = "error";
        self.alert.title = "Error 403";
        self.alert.message = "You don't have permission to view this info";
        self.canView = false;
      }



    });

  };



  self.loadData = function(){
    self.loadCPU();
    self.loadRAM();
    self.loadClicks();
  };


  //Refresh every 5 minutes
  setInterval(self.loadData,300000);


  self.loadData();


}]);
