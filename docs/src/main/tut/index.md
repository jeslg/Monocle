---
layout: home
title:  "Home"
section: "home"
position: 1
---

Monocle is an `Optics` library where Optics gather the concepts of `Lens`, `Traversal`,
`Optional`, `Prism` and `Iso`. Monocle is strongly inspired by Haskell [Lens](https://github.com/ekmett/lens).

### Getting Started

```scala
import sbt._
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.0"         // or "2.11.8", "2.10.6"

val libraryVersion = "1.3.2"     // or "1.4.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-generic" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-macro"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-state"   % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-refined" % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-unsafe"  % libraryVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % libraryVersion % "test" 
)

// for @Lenses macro support
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
```  

### Project Structure

In an attempt to be modular, Monocle is broken up into several modules:

* *core* - contains optics (e.g. `Lens`, `Prism`, `Traversal`) and type class definitions (e.g. `Index`, `Each`, `Plated`) and
  type class instances for standard library types and scalaz data types
* *macro* - macros to simplify the generation of optics
* *laws* - laws for the optics and type classes
* *generic* - optics and type class instances for `HList` and `Coproduct` from [shapeless](https://github.com/milessabin/shapeless)
* *refined* - optics and type class instances using refinement types from [refined](https://github.com/fthomas/refined)
* *unsafe* - optics that do not fully satisfy laws but that are very convenient. More details [here](unsafe_module.html)
* *tests* - tests that check optics and type class instances satisfy laws
* *bench* - benchmarks using jmh to measure optics performances
* *docs* - source for this website

### Copyright and License

All code is available to you under the MIT license, available [here](http://opensource.org/licenses/mit-license.php). 
The design is informed by many other projects, in particular Haskell [Lens](https://github.com/ekmett/lens).

Copyright the maintainers, 2016.