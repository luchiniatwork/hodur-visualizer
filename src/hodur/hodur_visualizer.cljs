(ns ^:figwheel-hooks hodur.hodur-visualizer
  (:require
   [goog.dom :as gdom]))

(println "This text is printed from src/hodur/hodur_visualizer.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))


;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))


(def data #js [#js {:key "Products"
                    :items #js [#js {:name "ProductID"
                                     :iskey true
                                     :figure "Decision"
                                     :color "purple"}
                                #js {:name "ProductName"
                                     :iskey false
                                     :figure "Decision"
                                     :color "purple"}]}])

(def data2 (clj->js [{:key "Bla1"} {:key "Bla4"}]))

(def links #js [])

(defn ^:export set-model []
  (set! (.-model js/myDiagram) (js/go.GraphLinksModel. data2)))

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (set-model)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
