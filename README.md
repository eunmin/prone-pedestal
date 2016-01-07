# prone-pedestal

Prone(https://github.com/magnars/prone) interceptor for Pedestal(https://github.com/pedestal/pedestal).

## Usage

Add `[prone-pedestal "0.1.1"]` to `:dependencies` in your `project.clj`, then:

Add it as a interceptor to pedestal service map:

```clojure
(require '[prone-pedestal.interceptor.exceptions :refer [exceptions]])

(defn exception-interceptor [service]
  (update-in service [::http/interceptors] #(vec (cons (exceptions) %))))

(defn run-dev [& args]
  (-> service/service
      (merge {:env :dev
              ::server/join? false
              ::server/routes #(deref #'service/routes)
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      server/default-interceptors
      (exception-interceptor) ;; need to call earlier than dev-interceptors.
      server/dev-interceptors
      server/create-server
      server/start))
```

## Debugging

Add `[prone "1.0.0"]` to `:dependencies` in your `project.clj`, then:

```clojure
(ns example
  (:require [prone.debug :refer [debug]]))

(defn myhandler [req]
  ;; ...
  (let [person (lookup-person (:id (:params req)))]
    (debug)))
```

## License

Copyright © 2016 Eunmin Kim

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
