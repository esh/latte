Array.prototype.subtract = function(t) {
	return this.filter(function(e) {
		return t.every(function(o) {
			return o != e;
		});
	});
}

String.prototype.trim = function() {
  return this.replace(/^\s+|\s+$/g, "");
}