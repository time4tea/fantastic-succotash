-- a get is the default in wrk so don't need to do anything here...a

done = function(summary, latency, requests)
    io.write(string.format("{ \"summary\": { \"requests\" : %d } }", summary.requests))
end

--   summary = {
--     duration = N,  -- run duration in microseconds
--     requests = N,  -- total completed requests
--     bytes    = N,  -- total bytes received
--     errors   = {
--       connect = N, -- total socket connection errors
--       read    = N, -- total socket read errors
--       write   = N, -- total socket write errors
--       status  = N, -- total HTTP status codes > 399
--       timeout = N  -- total request timeouts
--     }