var ap1 = new APlayer({
    element: document.getElementById('warningPlaying'),
    mini: true,
    autoplay: false,
    lrcType: false,
    mutex: true,
    preload: 'metadata',
    audio: [{
        name: '警报声',
        artist: '警报声',
        url: 'http://www.lrshuai.top/music/warn.mp3',
        cover: '',
        theme: '#ff0000'
    }]
});
ap1.on('play', function () {
    console.log('play');
});
ap1.on('play', function () {
    console.log('play play');
});
ap1.on('pause', function () {
    console.log('pause');
});
ap1.on('canplay', function () {
    console.log('canplay');
});
ap1.on('playing', function () {
    console.log('playing');
});
ap1.on('ended', function () {
    console.log('ended');
});
ap1.on('error', function () {
    console.log('error');
});
