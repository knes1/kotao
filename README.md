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

## Project structure

Kotao project typically consists of:

* config.yaml (configuration file)
* assets/ (directory with static assets, such as js and css files, images)
* content/ (directory with content files)
* templates/ (directory with templates)
* output/ (directory where generated content goes)

In a default configuration, Kotao will search for content in content directory, process the markdown markup (if files
end with .md extension), apply appropriate template to the content, generate the file and place it in the output folder.
Assets from the assets folder will also be copied to the output directory.

## Configuration

Kotlin uses yaml for configuration file format. Configuration file must be named config.yaml. Configuration file structure
consists of:

* collections
* pages
* vars
* repositories
* structure
