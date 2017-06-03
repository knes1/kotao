# Kotao - Introduction

Kotao is a simple static site generator built in [Kotlin](https://kotlinlang.org/). It allows to statically generate a 
web site using content from the file system or MongoDB database. It (optionally) uses Markdown for markup and FreeMarker 
as a template engine. In this is early version this is all there is :) Kotao's design goal is flexibility and plugin 
friendliness. Hopefully, it will support many content sources, markups, templating engines, javascript via Kotlin and 
Kotlin plugins.

## Installation

### Java

Kotao requires [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
to run:
* If you don't have Java installed, you can download it [here.](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
* You need to be able to run command `java` from your command prompt (`java` should be in your `PATH` environment variable).

### Download Kotao

* Download latest kotao zip file from [releases page](https://github.com/knes1/kotao/releases) and unzip the zip file in your file system.
* Add `bin/kotao` (or `bin\kotao.bat` in Windows) to your `PATH` environment variable.
* You should now be able to open command prompt and run `kotao --help` to verify kotao is installed.

## Quick Start

Create a new project called `mysite`:
```
kotao --init mysite
```
Change directory to `mysite` and run kotao:
```
cd mysite
kotao -s
```

Point your browser to http://localhost:8080


# Documentation 

The documentation is still work in progress as Kotao feature-set is evolving. 

## Project Structure

Kotao project typically consists of:

* config.yaml (configuration file)
* assets/ (directory with static assets, such as js and css files, images)
* content/ (directory with content files)
* templates/ (directory with templates)
* output/ (directory where generated content goes)

In a default configuration, Kotao will search for content in content directory, process the markdown markup (if files
end with .md extension), apply appropriate template to the content, generate the file and place it in the output folder.
Assets from the assets folder will also be copied to the output directory.

## Minimal Project

Minimal project consists of the configuration file `config.yaml` and a freemarker template for a single page.

Project directory:

    project_dir
     +--config.yml
     +--templates/index.ftlh

Contents of `config.yaml` configuration file for a single _simple page_ using given template:

    pages:
    - name: index

We insturct `kotao` to create a single _simple page_ called `index.html`. By convention `kotao` will use the template with the same name as the _simple page_ - `index.ftlh`. The configuration has a single section - `pages`. `pages` section contains configuration for so called _simple pages_, one-off pages that are rendered using a given data. After running `kotao`, `output` directory will be created containing a sigle page from a rendered template - `index.html`.

This minimal hello world setup is not particularly useful as it renders only a single page. Usually, starter project will at least contain `assets` folder (to hold static assets such as images, css and js files) and `content` folder to hold markdown content for pages. It could look like this:

    project_dir
     +-- config.yaml
     +-- templates/
          +-- _root.ftlh
     +--assets/
          +-- main.css
          +-- main.js
     +--content/
          +-- 2017-01-01-Hello_World.md
          +-- 2017-05-05-My_Second_Post.md

In this configuration `kotao` will read markdown files in `content` directory, use them as content available in default template for so called _file_repository_ - `_root.ftlh` and render results to output folder.


## Building the Site

To build the site just run `kotao` in the root project directory. 

### Dev Server

Kotao also comes with an embedded web server that can be used
while developing the site. To start kotao together with development server run:

`kotao -s`

You can then access the site on http://localhost:8080

When development server is running, it will watch for changes in the file system and will re-build the site
if the underlying files changes. Web page will be automatically reloaded in the browser once the build completes.

## Configuration

Kotlin uses yaml for configuration file format. Configuration file must be named config.yaml. Configuration file structure
consists of:

* pages
* collections
* vars
* repositories
* structure

### Pages

Pages are simple one-off pages that need to be generated. They are used when you need to generate an index page (page containing index or
references to a collection of pages) or when you simply need to generate one static one-off page.

### Collections

Collections are collections of pages rendered using same template and loading content from a _repository_. Simplest collections are collections loaded from file system. If a `content` folder is present in the project, `kotao` will read markdown files in the directory an render them using `_root.ftlh` template (if not configured differently).

## Sites Built With Kotao

* Sinful Spoonful Food Blog: www.sinfulspoonful.com


