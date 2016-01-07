(ns prone-pedestal.interceptor.exceptions
  (:require [io.pedestal.interceptor :refer [interceptor]]
            [ring.util.response :as ring]
            [ring.middleware.content-type :refer [content-type-response]]
            [prone.debug :as debug]
            [prone.middleware :as prone]))

(defn- asset-response [request asset]
  (-> (ring/response asset)
      (ring/header "Cache-Control" "max-age=315360000")
      (content-type-response request)))

(defn exceptions
  "Prone interceptor for pedestal."
  [& [{:keys [app-namespaces skip-prone?] :as opts}]]
  (interceptor
    {:enter
     (fn [{:keys [request] :as ctx}]
       (if-let [asset (prone/asset-url->contents (:uri request))]
         (assoc ctx :response (asset-response request asset))
         (assoc-in ctx [:bindings #'debug/*debug-data*] (atom []))))

     :leave
     (fn [{:keys [request] :as ctx}]
       (if (and
             (= clojure.lang.Atom (class debug/*debug-data*))
             (< 0 (count @debug/*debug-data*)))
         (assoc ctx :response
           (prone/debug-response request @debug/*debug-data*))
         ctx))

     :error
     (fn [{:keys [request] :as ctx} e]
       (when-not (and skip-prone? (skip-prone? request))
         (assoc ctx :response
           (prone/exceptions-response request e app-namespaces))))}))
