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


      <a class="navbar-brand" href="index.html">WallaLinks!</a>

      <div class="collapse navbar-collapse" id="js-navbar-collapse">
        <ul class="nav navbar-nav">
          <li ><a href="index.html">Home</a></li>
        </ul>
      </div>
  </div>
</div>

<div class="container">
    <img src="images/404.png" class="lockImage">
    <p class="lockText">404: URI not available</p>
    <p class="lockTextSecondary">We are sorry, but the URI requested is not available right now.</p>
</div>
<div class="container">
    <p class="lockTextSecondary"><strong>Last information about the requested URI:</strong></p>
    <p class="lockTextSecondary"><strong>URI: </strong><a href="${target}">${target}</a></p>
    <p class="lockTextSecondary"><strong>Not available since (last check): </strong>${date}</p>
</div>
</body>
</html>
