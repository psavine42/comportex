(ns org.nfrac.comportex.demos.directional-steps-1d
  (:require [org.nfrac.comportex.core :as cx]
            [org.nfrac.comportex.layer :as layer]
            [org.nfrac.comportex.encoders :as enc]
            [org.nfrac.comportex.util :as util]))

(def bit-width 300)
(def cat-bit-width 60)
(def numb-bit-width (- bit-width cat-bit-width))
(def numb-max 7)
(def numb-domain [0 numb-max])
(def n-on-bits 30)

(def params
  {:column-dimensions [800]
   :ff-potential-radius 0.2
   :ff-init-frac 0.3
   :spatial-pooling :local-inhibition
   :activation-level 0.04
   :boost-active-every 10000
   :depth 4})


(def higher-level-params
  (util/deep-merge
   params
   {:column-dimensions [500]
    :proximal {:max-segments 5}}))

(def initial-input-val [:up 0])

(defn input-transform
  [[dir i]]
  (let [new-i (-> (case dir
                    :up (inc i)
                    :down (dec i))
                  (min numb-max)
                  (max 0))
        new-dir (rand-nth [:up :down])] ;; mutation! impurity!
    [new-dir new-i]))

(defn input-seq
  "Returns an infinite lazy seq of sensory input values."
  []
  (iterate input-transform initial-input-val))

(def sensor
  (enc/sensor-cat
   [[0] (enc/category-encoder [cat-bit-width] [:down :up])]
   [[1] (enc/linear-encoder [numb-bit-width] n-on-bits numb-domain)]))

(defn build
  ([]
   (build params))
  ([params]
   (cx/network {:layer-a (layer/layer-of-cells params)}
               {:input sensor})))
