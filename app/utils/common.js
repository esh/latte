Function.prototype.curry = function() {
	var fn = this, args = Array.prototype.slice.call(arguments);
	return function() {
		return fn.apply(this, args.concat(Array.prototype.slice.call(arguments)))
    }
}

Array.prototype.subtract = function(t) {
	return this.filter(function(e) {
		return t.every(function(o) {
			return o != e;
		});
	});
}

Array.prototype.reduce = function(t, fn) {
	for(var i = 0 ; i < this.length ; i++) {
		t = fn(t, this[i])
	}
	return t
}

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g, "");
}

String.prototype.escapeURL = function() {
	return escape(this)
}

String.prototype.toBase64 = function() {
	var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="

	var output = ""
	var chr1, chr2, chr3
	var enc1, enc2, enc3, enc4
 	var i = 0

 	do {
      chr1 = this.charCodeAt(i++)
      chr2 = this.charCodeAt(i++)
      chr3 = this.charCodeAt(i++)

      enc1 = chr1 >> 2
      enc2 = ((chr1 & 3) << 4) | (chr2 >> 4)
      enc3 = ((chr2 & 15) << 2) | (chr3 >> 6)
      enc4 = chr3 & 63;

      if (isNaN(chr2)) {
         enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
         enc4 = 64;
      }

      output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) + 
         keyStr.charAt(enc3) + keyStr.charAt(enc4);
   } while (i < this.length);
   
   return output;
}
