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
				//sh "echo 'Checking out from ${config.repoUrl} ...'"
				//sh "git ${config.repoUrl}"
			}
			stage ('Build') {
				bat(echo 'building ${config.projectName} ...')
			}
			stage ('Tests') {
				parallel 'static': {
					sh "echo 'shell scripts to run static tests...'"
				},
				'unit': {
					sh "echo 'shell scripts to run unit tests...'"
				},
				'integration': {
					sh "echo 'shell scripts to run integration tests...'"
				}
			}
			stage ('Deploy') {
				sh "echo 'deploying to server ${config.serverDomain}...'"
			}
		} catch (err) {
			currentBuild.result = 'FAILED'
			throw err
		}
	}
}