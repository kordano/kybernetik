(ns kybernetik.app
  (:require [kybernetik.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
