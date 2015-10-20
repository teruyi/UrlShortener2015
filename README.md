# Web Engineering 2015-2016 / UrlShortener2015
[![Build Status](https://travis-ci.org/UNIZAR-30246-WebEngineering/UrlShortener2015.svg)](https://travis-ci.org/UNIZAR-30246-WebEngineering/UrlShortener2015)

This is the shared repository for the project developed in this course. Go to the [wiki](wiki) to start your project.

## Projects

* [Common](common) is the project that provides a minimum set of shared features.
* [Demo](demo) is the template project and the sandbox for solving blocking issues.

## Teams

* [Alizarin Crimson](alizarin-crimson): Javier, Jorge, Alejandro
* [Bangladesh Green](bangladesh-green): Luis, Raúl, Ismael, Sergio
* [Candy Pink](candy-pink): Alberto, Íñigo, Santiago, David
* [Dim Gray](dim-gray): Ramón, Óscar, Guillermo 
* [Eerie Black](eerie-black): Jorge, Adrián, Marcos 
* [Fuzzy Wuzzy](fuzzy-wuzzy): Christian, Alberto, Diego
* [Golden Brown](golden-brown): Óscar, Daniel, Eduardo
* [Heat Wave](heat-wave): Íñigo, Marcos, Carlos
* [Imperial Red](imperial-red): Javier, Jorge, Alejandro
* [Japanese Violet](japanese-violet): Aron, Agustín, Alejandro
* [Lavender Gray](lavender-gray): Rubén, Cristian, Daniel
* [Navajo White](navajo-white): Jorge, Alberto, Adrian

## Starting procedure

* The team leader forks this repository
* Each team member forks the fork of the respective team leader
* Import your own fork into eclipse. You must import the common project, the demo project and your project. For example, if your project is _BangladeshGreen_ you must import `UrlShortener2015.common`, `UrlShortener2015.demo` and `UrlShortener2015.BangladeshGreen`. Other projects are optional.
* Create the folder `src\main\java` in your project.
* Copy the contents of `src\main\java` from `UrlShortener205.demo` into your project.
* Rename the package `urlshortener2015.demo` to your color. For example if your project is _BangladeshGreen_ you must rename it to `urlshortener2015.bangladeshgreen`.
* Test that your program run using command line (`gradle run`) or within eclipse (either as Java application or as Gradle application).
* Do `$ curl -v -d "url=http://www.unizar.es/" -X POST http://localhost:8080/link` and check that appears a line in the console that contains `u.d.web.UrlShortenerControllerWithLogs`

Now you can start to add new functionality to your project.

## Push & Pull

* Each team member should work in its local repository.
* Periodically, each team member must __push__ its work to its repository in GitHub and then make a __pull request__ for sent your changes to the repository of the team.
* Periodically, each team member must __pull__ from GitHub to fetch and merge changes from remote repositories (your team changes or changes in the original repository).
