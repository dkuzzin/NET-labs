const http = require('http')
const fs = require('fs')
const path = require('path')

const PORT = 3000
const HOST = '127.0.0.1'

const fileForSave = 'data.txt'
const publicAccessFolder = "public"

const MIME_TYPES = {
    ".html": "text/html; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".js": "application/javascript; charset=utf-8",
    ".txt": "text/plain; charset=utf-8",
    ".png": "image/png",
    ".jpg": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".gif": "image/gif",
    ".svg": "image/svg+xml"
};


const server = http.createServer((req, res) =>{
    console.log(req.method, req.url)

    if (req.method === 'GET'){
        let filepath;
        if (req.url === '/'){
            filepath = path.join(publicAccessFolder, 'index.html');
        }else{
            filepath = path.join(publicAccessFolder, req.url.slice(1));
        }

        
        fs.readFile(filepath, (err, data) =>{
            if (err){
                res.writeHead(404, {"Content-Type": "text/plain; charset=utf-8"});
                res.end('404 Not Found');
                return;
            }
            const ext = path.extname(filepath)

            const contentType = MIME_TYPES[ext] || "application/octet-stream";
            res.writeHead(200, {
                'Content-Type': contentType,
                'Content-Length': data.length
            });
            res.end(data);  

        })

    }else if (req.method === "POST") {
        let body = ""

        req.on("data", chunk => {
            body += chunk;
        })
        
        req.on("end", ()=>{
            body += "\n\n"
            const decoded = decodeURIComponent(body.replace(/\+/g, " ")); // \+ - символ пробела g - все вхождения
            console.log(decoded);
            fs.appendFile(fileForSave, body, err =>{
                if (err){
                    res.writeHead(500);//Внутренняя ошибка сервера
                    res.end("500: Server error");   
                    return;
                }
                const successPage = path.join(publicAccessFolder, 'success.html');

                fs.readFile(successPage, (err, data) => {
                    if (err) {
                        console.log("Не удалось прочитать 'success.html'")
                        res.writeHead(200, { "Content-Type": "text/plain; charset=utf-8" });
                        res.end("Данные сохранены");
                        return;
                    }
                    res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
                    res.end(data);
                });

            })
        })
    }else {
        res.writeHead(405);
        res.end("405: Method Not Allowed"); //Сервер работает но данный HTTP метод для него не разрешен
    }
})

server.listen(PORT, HOST, () => {
    console.log("SERVER WAS STARTED PORT: ", PORT, " HOST: ", HOST);
});