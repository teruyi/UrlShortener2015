<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
  <meta name="description" content="">
  <meta name="viewport" content="width=device-width">
  <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
  <!-- build:css(.) styles/vendor.css -->
  <!-- bower:css -->
  <link rel="stylesheet" href="https://bootswatch.com/cosmo/bootstrap.css" />
  <!-- endbower -->
  <!-- endbuild -->
  <!-- build:css(.tmp) styles/main.css -->
  <link rel="stylesheet" href="styles/main.css">
  <!-- endbuild -->
</head>
<body>
<!--[if lte IE 8]>
<p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->

<!-- Add your site or application content here -->

  <navigation-bar>
    <div class="navbar navbar-default" role="navigation" style="margin:0px; !important">


      <a class="navbar-brand" href="/">WallaLinks!</a>

      <!-- ngIf: currentPath()!='/login' --><div class="collapse navbar-collapse ng-scope" id="js-navbar-collapse" ng-if="currentPath()!='/login'">
      <ul class="nav navbar-nav">
        <li ng-class="{'active':currentPath().indexOf('shorten')>0}" class=""><a href="/"><i class="glyphicon glyphicon-home"></i> Home</a></li>
      </ul><!-- end ngIf: logged() -->
      <!-- ngIf: !logged() -->
    </div><!-- end ngIf: currentPath()!='/login' -->
    </div>
  </navigation-bar>


<table class="table table-bordered table-hover">
  <caption>Infomación de la URL ${url}</caption>
  <thead>
  <tr>
    <th>Target</th>
    <th>Date</th>
    <th>Count</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>${target}</td>
    <td>${date}</td>
    <td>${count}</td>
  </tr>
  </tbody>
</table>
</body>
</html>
