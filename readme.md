Just some examples from the [Hypermedia Systems book](https://hypermedia.systems) but adopted for Clojure. Obviously this is just for educational purposes only and you should not actually run this server on the internet.

**Points of interest:**

- SPA-like - No hard reloads, no FOUC
- Although HTMX script is included, I didn't have to write any JS.
- The entire app is ~300 lines? The equivalent for an SPA would likely be more, not to mention the hassle of the transpilation necessary etc.

**Stack:**

- http server - ring/reitit
- html - hiccup
- style - bootstrap
- ajax - htmx

![screenshot-3](https://github.com/aburd/hypermedia-systems-clj-examples/assets/6701630/281468e3-e964-4254-b70b-6cb23fe141b1)

**Features**

- Generic in-memory CRUD feature at /contacts
- An "archiver" feature, which will just tar.gzip any directory on your harddisk (I just needed something CPU intensive enough to experiment with making a progress-bar)
  - requires `tree` & `tar`
