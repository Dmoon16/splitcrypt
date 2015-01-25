(ns splitcrypt.core
	[:require 
		[clojure.java.io :as io]
		[eqvkey.core :refer [eqv make-bit-seq]]]
	(:gen-class :main true))

(defn lazy-open [fpath]
  (defn helper [rdr]
    (lazy-seq
      (let [bt (io/.read rdr)]
      	(if (not (= bt -1))
        	(cons bt (helper rdr))
        	(do 
        		(io/.close rdr) 
        		nil)))))
  (lazy-seq
    (do (helper (io/reader fpath)))))

(defn streams-by-key [stream key-stream]
	(defn key-reducer [acc el]
		(let [[lft rgt ks] acc]
			(if (= 1 (first ks))
				[(conj lft el) rgt (rest ks)]
				[lft (conj rgt el)  (rest ks)])))
	(let [[l r] 
			(reduce key-reducer 
				['() '() (take (count stream) 
					(cycle key-stream))] 
				stream)]
		[(reverse l) (reverse r)]))


(defn bits-to-byte [byt]
	(letfn [(bit-red [acc el] 
		(+ (bit-shift-left (int acc) 1) (int el)))]
		(reduce  bit-red byt)))

(defn bit-list-bytes [bits]
	(map bits-to-byte (partition-all 8 bits)))

(defn split-with-key 
	[fpath k]
	(let [ks (eqv k 3) 
		fs (make-bit-seq (lazy-open fpath))
		[first-half second-half] (streams-by-key fs ks)
		first-name (format "%s-part1" fpath)
		last-name (format "%s-part2" fpath)]
		(with-open [wr1 (io/writer first-name)] 
			(doseq [byt (bit-list-bytes first-half)] (io/.write wr1 byt)))
		(with-open [wr2 (io/writer last-name)] 
			(doseq [byt2 (bit-list-bytes second-half)] (io/.write wr2 byt2)))
		[(count fs) (count first-half) (count second-half)]))

(defn -main [& args]
	(let [fpath (first args) k (nth args 1 "")]
	    (println "Your key: " k)
	    (println "Your file: " fpath)
	    (println "[Original/Part 1/Part 2]: " (split-with-key fpath k))))