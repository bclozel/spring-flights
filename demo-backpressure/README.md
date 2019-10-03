## RSocket Back Pressure Demo

Small app to demonstrate RSocket back pressure.

### Step 1

Start an RSocket server that streams dictionary words through the
[RSocket CLI](https://github.com/rsocket/rsocket-cli):

```
./rsocket-cli -i=@/usr/share/dict/words --server --debug tcp://localhost:8765
```

### Step 2

Run the [demo app](io/spring/demo/backpressure/DemoBackpressureApplication.java)
and enter demand values on the console.