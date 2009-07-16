#!/usr/bin/ruby

require 'net/http'
require 'uri'
require 'tmail'

mail = TMail::Mail.parse($stdin.read)

title = mail.subject 
tags = ""
photo = ""
ext = ""

if mail.multipart? then
	mail.parts.each do |m|
		case m.main_type
		when "text"
			tags += m.body.strip
		when "image"
			photo = m.base64_encode
			ext = m.disposition_param("filename").split(".")[1]
		end
	end
end

url = URI.parse('http://localhost:8080/api/create')
req = Net::HTTP::Post.new(url.path, initheader = {'Content-Type' => 'application/json'})
req.basic_auth 'username', 'password'
req.body  = "({\"title\":\"#{title}\",\"photo\":\"#{photo}\",\"ext\":\"#{ext}\",\"tags\":\"#{tags.chomp}\",\"twit\":true})"

res = Net::HTTP.new(url.host, url.port).start {|http| http.request(req) }
case res
when Net::HTTPSuccess, Net::HTTPRedirection
	# OK
else
	res.error!
end
