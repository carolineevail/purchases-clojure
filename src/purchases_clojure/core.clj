(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]
            [hiccup.core :as h])
  (:gen-class))


(defn read-purchases []
;  (println "Type a category [Furniture, Alcohol, Toiletries, Shoes, Food, Jewelry] and hit enter to view all purchases in that category.")
  (let [purchases (slurp "purchases.csv")
        ;text (read-line)
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                         (str/split line #","))
                      purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                       purchases)
        purchases (walk/keywordize-keys purchases)]
;        purchases (filter (fn [line]
;                            (= (:category line) text))
;                       purchases)]
    ;(spit "filtered_purchases.edn" (pr-str purchases))
    purchases))

(defn categories-html [purchases]
  (let [all-categories (map :category purchases)
        unique-categories (set all-categories)
        sorted-categories (sort unique-categories)]
    [:div
     (map (fn [category]
            [:span
             [:a {:href (str "/?category=" category)} category]
             " "])
       sorted-categories)]))

(defn purchases-html [purchases]
  [:ol
   (map (fn [purchase]
          [:li (str (:customer_id purchase) " " (:date purchase) " " (:credit_card purchase) " " (:cvv purchase) " " (:category purchase))])
        purchases)])


(c/defroutes app
  (c/GET "/" request
    (let [params (:params request)
          category (get params "category")
          category (or category "Shoes")
          purchases (read-purchases)
          filtered-purchases (filter (fn [purchase]
                                       (= (:category purchase) category))
                              purchases)]
      (h/html [:html 
               [:body 
                (categories-html purchases)
                (purchases-html filtered-purchases)]]))))

(defn -main []
  (j/run-jetty (p/wrap-params app) {:port 3000}))

