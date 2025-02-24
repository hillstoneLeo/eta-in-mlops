# ETA

This is a demo project to use dvc and mlflow for MLops.

## Prerequisites

* docker: run zeek with `docker run zeek/zeek zeek ...`;
* dvc;
* babashka;

## Usage

After clone the repo, run in the project root folder:
```sh
dvc dag
dvc stage list
dvc pull
dvc repro
```

You can reproduce intermediate data with:
```sh
rm -rf build .dvc/cache
dvc repro
```
