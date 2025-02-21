#!/usr/bin/env bb

(require '[babashka.cli :as cli]
         '[babashka.process :refer [shell]]
         '[babashka.fs :as fs])

(def build-dir "./build/zlogs")  ; /data/zlogs: from "outs" section in dvc.yaml

(defn parse-pcap [inp]
  (cond
    (not (fs/exists? inp)) (println (str "Target file " inp " not exists!"))
    (not= (fs/extension inp) "pcap") (println (str "Ignored: target file " inp " must has extension pcap!"))
    :else (let [file-name (fs/file-name inp)
                log-folder (fs/strip-ext inp)
                parent-folder (fs/parent inp)]
            (println (str "Write parsed logs of " inp " into " parent-folder))
            (fs/delete-tree log-folder)
            (fs/create-dir log-folder)
            (shell "docker"
                   "run" "--rm"
                   "-v" (str "./" parent-folder ":/data")
                   "zeek/zeek" "zeek"
                   "LogAscii::use_json=T"
                   (str "Log::default_logdir=/data/" (fs/strip-ext file-name))
                   "-r" (str "/data/" file-name))
            (fs/move log-folder build-dir))))

(defn main [args]
  (if (= (count args) 0)
    (println "Usage: parse-traffic.clj file1.pcap file2.pcap ...")
    (do
      (fs/delete-tree build-dir)
      (fs/create-dirs build-dir)
      (doseq [inp args]
        (parse-pcap inp)))))

(main *command-line-args*)
