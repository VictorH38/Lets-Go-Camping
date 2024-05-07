export function Header() {

    return (<div style={{width: "100%", padding: "0 20%", position: "relative", backgroundColor: "#123524"}}>
        <header
            style={{textAlign: "center"}}
        >
            <a href="/"
               className="align-items-center mb-3 mb-md-0 me-md-auto text-dark text-decoration-none">
                <div>
                    <span className="fs-4" style={{color: "white", fontSize: "2.3em"}}>Let's Go Camping</span>
                </div>
                <div>
                    <span className="fs-4" style={{color: "white"}}>Team 15</span>
                </div>
            </a>
        </header>
    </div>)
}