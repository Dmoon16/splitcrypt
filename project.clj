(defproject splitcrypt "0.1.0-SNAPSHOT"
	:description "Split a file according to a key's equivalent"
	:url "https://github.com/adrianrosian/splitcrypt.git"
	:license {:name "Eclipse Public License"
	:url "http://www.eclipse.org/legal/epl-v10.html"}
	:dependencies [
		[org.clojure/clojure "1.6.0"]
		[org.clojars.adrianr/eqvkey "0.3"]
	]
	:main ^:skip-aot splitcrypt.core
	:target-path "target/%s"
	:profiles {:uberjar {:aot :all}})
