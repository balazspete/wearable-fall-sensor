
express = require('express')
app = express()
server = require('http').Server app
io = require('socket.io').listen server
lessMiddleware = require 'less-middleware'

server.listen 3000

app.use(lessMiddleware "/less", {
  dest: "/css"
  pathRoot: "#{__dirname}"
  debug: true
  force: true
})

app.use '/css', express.static("#{__dirname}/css")

app.get '/', (req, res) ->
  res.sendfile "#{__dirname}/index.html"

io.sockets.on 'connection', (socket) ->
  socket.on 'measurement', (data) ->
    console.log data
    socket.broadcast.emit 'measurement', data
