(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk])
  (:gen-class))


(defn -main []
  (println "Type a category [Furniture, Alcohol, Toiletries, Shoes, Food, Jewelry] and hit enter to view all purchases in that category.")
  (let [purchases (slurp "purchases.csv")
        text (read-line)
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                         (str/split line #","))
                      purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                       purchases)
        purchases (walk/keywordize-keys purchases)
        purchases (filter (fn [line]
                            (= (:category line) text))
                       purchases)]
    (spit "filtered_purchases.edn" (pr-str purchases))
    purchases))

