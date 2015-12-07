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
  <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.css" />
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
<div class="header">
  <div class="navbar navbar-default" role="navigation">
    <div class="container">



      <a class="navbar-brand" href="index.html"> WallaLinks! </a>


      <div class="collapse navbar-collapse" id="js-navbar-collapse">
        <ul class="nav navbar-nav">
          <li class="active"><a href="#/">Home</a></li>
        </ul>
      </div>
    </div>
  </div>
</div>

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