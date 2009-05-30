(function(size) {
	function Node(prev, next, value) {
		var prev = prev
		var next = next
		var value = value

  		return {
			prev: prev,
			next: next,
			value: value
		}
	}

	var size = size
	var count = 0
	var data = new Object()
	var head = new Node(null, null, null)
	var tail = new Node(null, null, null)
	head.next = tail
	tail.prev = head

	function get(key) {
		if(data[key] == undefined) return undefined
	
		var t = data[key]
		t.prev.next = t.next
		t.next.prev = t.prev

		t.next = head.next
		t.prev = head
		head.next.prev = t
		head.next = t
	
		return t.value.value
	}

	function put(key, value) {
		if(data[key] != undefined) {
			data[key].value.value = value
		} else {
			count++
			var t = head.next
			head.next = new Node(head, t, {key:key, value:value})
			t.prev = head.next
			data[key] = head.next
	
			if(count > size) {
				delete data[tail.prev.value.key]
				tail.prev = tail.prev.prev
				tail.prev.next = tail
				count--
			}
		}
   }

   return {
       get: get,
       put: put
   }
})
