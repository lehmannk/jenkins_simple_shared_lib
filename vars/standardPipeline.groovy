def call(body) {

	def config = [:]
	body.resolveStrategy = Closure.DELEGATE_FIRST
	body.delegate = config
	body()

	node {
		// Clean workspace before doing anything
		deleteDir()

		try {
			stage ('Clone') {
				echo 'Checking out ' + config.repoUrl + ' ...'
				git config.repoUrl
			}
			stage ('Build') {
				echo 'building ' + config.projectName + ' ...'
				echo 'skipping tests: ' + config.skipTests
				withMaven(maven:'M3') {
					mvn -DskipTests clean package
				}
			}
			stage ('Tests') {
				parallel 'static': {
					echo 'shell scripts to run static tests...'
				},
				'unit': {
					echo 'shell scripts to run unit tests...'
				},
				'integration': {
					echo 'shell scripts to run integration tests...'
				}
			}
			stage ('Deploy') {
				echo 'deploying to server ${config.serverDomain}...'
				def deployOptions = 'no\nyes'
				def userInput = input(
				  id: 'userInput', message: 'Are you prepared to deploy?', parameters: [
				  [$class: 'ChoiceParameterDefinition', choices: deployOptions, description: 'Approve/Disallow deployment', name: 'deploy-check']
				  ]
				)
				echo "you selected: ${userInput}"
			}
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}