Der PaziFist
=============


Aliens wollen die Welt unterdrücken und beleben hierfür ehemalige Diktatoren wieder. Aus Versehen wird
auch Jesus wiederbelebt, der nun die Welt vor der Unterdrückung retten möchte.

Das Spiel startet in einem Tutorial und enthält Hilfetexte, Bedienung und Funktionen sind also selbsterklärend.

Auf der Projektseite http://luetge.github.com/der-pazifist/ befinden sich Links zu ausführbaren Versionen
des PaziFisten. Um den Quellcode selbst zu kompilieren können entweder die vorhandenen Projektdateien
in Eclipse geladen werden oder das Projekt kann mit Apache Ant kompiliert werden, was ein ausführbares
Jar-Archiv dist/Der-PaziFist.jar erzeugt.

Der PaziFist basiert ursprünglich auf dem Projekt "jaderogue" von Jeff Lund (https://code.google.com/p/jaderogue/)
und wurde im Rahmen des Softwarepraktikums im WS 2012/13 an der Freien Universität Berlin entwickelt. Die
ursprüngliche Copyright Notice von jaderogue findet sich in contrib/jaderogue_license.txt.

Der PaziFist benutzt weiterhin Teile der slick2d Library (slick2d.org) und von LWJGL (lwjgl.org). Weitere
Informationen hierzu finden sich in den READMES in src/lib und src/lib/lwjgl.


Unter bestimmten Versionen von Mac OS X, auf denen Java 7 installiert ist, kann es zu einem Absturz der
Java Virtual Machine kommen. Die Ursache hierfür ist ein Problem von LWJGL, Der PaziFist ist also für diesen
Fehler nicht selbst verantwortlich. Bis dieser Fehler behoben ist, kann der PaziFist auch ohne LWJGL gestartet
werden, indem ihm das Argument "-noglview" übergeben wird oder die Java System Property "useglview" auf "false"
gesetzt wird. Leider ist in dieser Version allerdings die Darstellung deutlich langsamer und es kann zu Problemen
bei der Tastatureingabe kommen.



Copyright (c) 2012
Eric Badstübner, Daniel Kirchner, Daniel Lütgehetmann, Niklas Rughöft, Toan Tran Ngoc, Markus Winzer
