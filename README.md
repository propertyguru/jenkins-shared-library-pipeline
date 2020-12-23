1. can make use of lock and milestones
https://www.jenkins.io/blog/2016/10/16/stage-lock-milestone/

2. method pointer:
def fun = stash.&merge
println(fun())

def func(List stashes) {
    for (def stash : stashes) {
        def fun = stash.&merge
        println(fun())
    }
}

def stashes = [(new Stash(1))]
func(stashes)

class Stash {
    def num
    Stash(num) {
        this.num = num
        println("constructor")
    }
    
    def merge() {
        return this.num
    }
    
    def getPrince() {
        return 'prince'
    }
    
}

@interface Page {
    int statusCode() default 200
}

@Page(statusCode=404)
void notFound() {
    println(statusCode)
}
notFound()

def x = new Stash(2)
println(x.prince)