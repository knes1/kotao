# Kotao - Introduction

Kotao is a simple static site generator built in [Kotlin](https://kotlinlang.org/). It allows to statically generate a 
web site using content from the file system or MongoDB database. It (optionally) uses Markdown for markup and FreeMarker 
as a template engine. In this is early version this is all there is :) Kotao's design goal is flexibility and plugin 
friendliness. Hopefully, it will support many content sources, markups, templating engines, javascript via Kotlin and 
Kotlin plugins.

# Documentation 

The documentation is still work in progress as Kotao feature-set is evolving. Please refer to following projects as
samples:

* [kotao-getting-started](https://github.com/knes1/kotao-getting-started)

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

This minimal hello world setup is not particulary useful as it renders only a single page. Usualy, starter project will at least contain `assets` folder (to hold static assets such as images, css and js files) and `content` folder to hold markdown content for pages. It could look like this:

    project_dir
     +-- config.yml
     +-- templates/
          +-- _root.ftlh
     +--assets/
          +-- main.css
          +-- main.js
     +--content/
          +-- 2016-01-01-Hello_World.md
          +-- 2016-02-03-My_Second_Post.md

In this configuration `kotao` will read markdown files in `content` directory, use them as content available in default template for so called _file_repository_ - `_root.ftlh` and render results to output folder.

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


