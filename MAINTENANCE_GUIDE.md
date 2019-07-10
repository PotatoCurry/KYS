# KYS Maintenance Guide
**Written by Damian Lall, Class of 2021**

Given that you’re reading this, I can safely assume that KYS has messed up in some way, and you’ve been charged with fixing it.¹
You poor soul.

#### Easy Way Out
Your first course of action should probably be to contact the original (and practically sole) creator of KYS, Damian Lall.
However, this is the lazy man’s way out, and there is a possibility he will completely ignore your cries for help, being as antisocial and mean-spirited as he is.
In fact, he may even remember certain events from his time at Clements that do nothing but inspire malevolence towards the school, and lead to him to add more fuel to the fire in a misguided attempt at revenge.

## Build Process
Before you start trying to fix whatever is wrong with KYS, you’ll need to be able to build and test it locally.
If you’re familiar with some of these tools, this section will be easy.
If not, have fun learning how to use all sorts of (possibly) outdated tools with outdated configurations.²

### Dependencies
KYS uses Gradle to manage it’s dependencies.
You can learn how to use Gradle manually, but for now I recommend using an IDE with built-in support, like IntelliJ IDEA (in which KYS was originally programmed in 2019).
Once a Gradle build has been successfully run, check to see if (most of) the scary warnings and red squiggles in the code have gone away.
Familiarize yourself with these tools (primarily Ktor) before proceeding.

### Build Environment
Due to the nature of KYS being open-source, several configurations and tokens are stored as environmental variables or in configuration files.
If Damian turned you away earlier, he should at least be kind enough to hand over these, otherwise you are well and truly screwed.
If he didn’t entrust the keys to the YES coordinators, you may as well just give up now.

Assuming everything previously described went off without a hitch, you’ll then need to replicate the commands in [.travis.yml](.travis.yml).
This would also be a good time to mention you ought to be working on this on a Linux computer.
I did develop some of this on my Windows 10 desktop, but it’s not worth the hassle for me to explain the necessary steps.
Spin up an Ubuntu VM if you need to.

### Unit Tests
Great, you’ve (hopefully) sort of got things working.
The next step is to run the tests I oh-so-kindly made in special preparation for you.
Do they run fine?
Great, that’s actually worse for you as the source of your problems is further along in the build toolchain.
Keep reading.
Does it produce an error? Great, now it’s just a matter of simple debugging.
(Note that you may have to remove the tests which specifically check the accuracy of Damian’s YES hours, as I may have been removed from the database.)

## Build Toolchain
Great, you’ve finally finished replicating the build environment, something that would have taken an experienced programmer less than thirty minutes, but (probably) neared hours for you.
Oh well, let’s move onto getting your code running.

### GitHub Repository
You should really know how to use Git.
Learn the basics before continuing.

### Travis CI
Once you push to the master branch of the repository, Travis will automatically set up the build environment (following the instructions in the aforementioned yml file) and run your tests to make sure everything works.
You have access to the logs if CI fails.
READ THEM.

### Heroku
Subsequent to a successful Travis build, the code will be handed over to Heroku, which will run until a new push comes through or it crashes.
Preferably not the latter.
You also have access to these logs.
They’re probably useful if you still have yet to find the problem.

### Logging
KYS uses Logback to handle logging.
Logs are categorized by importance and can be filtered through configuration of the [logback.xml](resources/logback.xml) file.
Logs of level WARN or ERROR are reported to Sentry, which provides a dashboard of issues.
Sentry can provide notifications upon receiving such events, along with context that can help you narrow down the issue.
To gain a deeper insight in to what caused the problem, you can consult the previously discussed Heroku logs.

## Help! Nothing works!
Too bad.

## Extra
Maybe you’ve enjoyed working with KYS and want to do more (Stockholm Syndrome?) or are just looking for things to do in an attempt to earn easy YES hours as "maintainer" of KYS.
Whatever the reason, here are some cheap things you can do.
* Upgrade Gradle
* Upgrade dependencies
* Upgrade Kotlin
* Convert the Gradle build file to use Kotlin Gradle DSL
* Challenge mode: Build an authenticated web interface which allows the YES coordinators to directly enter records and gives KYS the responsibility of maintaining the Google Sheets database
* Ultra challenge mode: Remake the database with something more professional than GOOGLE SHEETS

## Footnotes
1. Or perhaps you’re just a technically-inclined student who followed the GitHub link from the website, in which case you now have a sneak peek into the hellhole that will greet one of your future Clements alumnus.

2. I have no idea when you’re reading this.
If it‘s a year or two after I left you won’t have much trouble, but if this is five, god forbid ten years from my departure, I pray for your sanity.
